package org.example.hanmo.vaildate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hanmo.dto.chat.response.ChatMessage;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.ChatServiceException;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChatValidate {
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
}