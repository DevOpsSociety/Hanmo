package org.example.hanmo.vaildate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hanmo.dto.chat.response.ChatMessage;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.ChatServiceException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ChatValidate {

	public static String getRoomIdByUser(StringRedisTemplate redis, Long userId) {
		String roomIdStr = redis.opsForValue().get("userRoom:" + userId);
		if (roomIdStr == null) {
			throw new ChatServiceException("채팅방을 찾을 수 없습니다.", ErrorCode.CHAT_ROOM_NOT_FOUND);
		}
		return roomIdStr;
	}

	public static void ensureRoomExists(StringRedisTemplate redis, String roomKey) {
		if (!Boolean.TRUE.equals(redis.hasKey(roomKey))) {
			throw new ChatServiceException("채팅방이 존재하지 않습니다.", ErrorCode.CHAT_ROOM_NOT_FOUND);
		}
	}

	public static void ensureUserHasAccess(StringRedisTemplate redis, String roomKey, Long userId) {
		String participants = redis.opsForValue().get(roomKey);
		if (participants == null || Arrays.stream(participants.split(","))
			.noneMatch(id -> id.trim().equals(String.valueOf(userId)))) {
			throw new ChatServiceException("채팅방 접근 권한이 없거나 만료되었습니다.", ErrorCode.CHAT_ROOM_EXPIRED);
		}
	}

	public static List<ChatMessage> parseHistory(StringRedisTemplate redis,
		ObjectMapper mapper, String historyKey) {
		List<String> raw = redis.opsForList().range(historyKey, 0, -1);
		if (raw == null) return List.of();
		return raw.stream()
			.map(json -> parseChatMessage(mapper, json))
			.collect(Collectors.toList());
	}

	public static ChatMessage parseChatMessage(ObjectMapper mapper, String json) {
		try {
			return mapper.readValue(json, ChatMessage.class);
		} catch (Exception e) {
			throw new ChatServiceException("채팅 내역 파싱에 실패했습니다.", ErrorCode.CHAT_MESSAGE_SEND_FAILED);
		}
	}

	public static void handleSaveMessage(StringRedisTemplate redis,
		ObjectMapper mapper, ChatMessage msg, String historyKey, String roomKey) {
		try {
			String json = mapper.writeValueAsString(msg);
			redis.opsForList().rightPush(historyKey, json);
			Long ttl = redis.getExpire(roomKey);
			if (ttl != null && ttl > 0) {
				redis.expire(historyKey, ttl, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			throw new ChatServiceException("메시지 전송에 실패했습니다.", ErrorCode.CHAT_MESSAGE_SEND_FAILED);
		}
	}

	public static String getCreatedAt(StringRedisTemplate redis, String roomKey) {
		Object createdAtObj = redis.opsForHash().get(roomKey, "createdAt");
		if (createdAtObj == null) {
			throw new ChatServiceException("채팅방 생성일시를 찾을 수 없습니다.", ErrorCode.CHAT_ROOM_NOT_FOUND);
		}
		return createdAtObj.toString();
	}

	public static int getParticipantCount(StringRedisTemplate redis, String roomKey) {
		Object countObj = redis.opsForHash().get(roomKey, "participantCount");
		if (countObj == null) {
			throw new ChatServiceException("참여자 수를 찾을 수 없습니다.", ErrorCode.CHAT_ROOM_NOT_FOUND);
		}
		try {
			return Integer.parseInt(countObj.toString());
		} catch (NumberFormatException ex) {
			throw new ChatServiceException("유효하지 않은 참여자 수 형식입니다.", ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}