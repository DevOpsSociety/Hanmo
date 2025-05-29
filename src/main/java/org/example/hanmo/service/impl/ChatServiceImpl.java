package org.example.hanmo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.chat.response.ChatMessage;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.ChatServiceException;
import org.example.hanmo.service.ChatService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	private String roomKey(String roomId)    { return "chatRoom:"    + roomId; }
	private String historyKey(String roomId) { return "chatHistory:" + roomId; }

	@Override
	public void checkAndJoin(String roomId, Long userId) {
		String key = roomKey(roomId);
		// 채팅방 존재 여부 확인
		if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
			throw new ChatServiceException("채팅방이 존재하지 않습니다.", ErrorCode.CHAT_ROOM_NOT_FOUND);
		}
		String participants = redisTemplate.opsForValue().get(key);
		// 참여 권한 및 만료 여부 확인
		if (participants == null || Arrays.stream(participants.split(",")).noneMatch(id -> id.trim().equals(String.valueOf(userId)))){
			throw new ChatServiceException("채팅방 접근 권한이 없거나 만료되었습니다.", ErrorCode.CHAT_ROOM_EXPIRED);
		}
	}

	@Override
	public void checkParticipant(String roomId, Long userId) {
		checkAndJoin(roomId, userId);
	}

	@Override
	public List<ChatMessage> loadHistory(String roomId) {
		List<String> raw = redisTemplate.opsForList().range(historyKey(roomId), 0, -1);
		if (raw == null) {
			return List.of();
		}
		return raw.stream().map(json -> {
			try {
				return objectMapper.readValue(json, ChatMessage.class);
			} catch (Exception e) {
				throw new ChatServiceException("채팅 내역 파싱에 실패했습니다.", ErrorCode.CHAT_MESSAGE_SEND_FAILED);
			}
		}).collect(Collectors.toList());
	}

	@Override
	public void saveMessage(ChatMessage message) {
		String roomKey = roomKey(message.getRoomId());
		if (!Boolean.TRUE.equals(redisTemplate.hasKey(roomKey))) {
			throw new ChatServiceException("채팅방이 존재하지 않습니다.", ErrorCode.CHAT_ROOM_NOT_FOUND);
		}

		try {
			String json = objectMapper.writeValueAsString(message);
			redisTemplate.opsForList().rightPush(historyKey(message.getRoomId()), json);
			Long ttl = redisTemplate.getExpire(roomKey);
			if (ttl != null && ttl > 0) {
				redisTemplate.expire(historyKey(message.getRoomId()), ttl, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			throw new ChatServiceException("메시지 전송에 실패했습니다.", ErrorCode.CHAT_MESSAGE_SEND_FAILED);
		}
	}
}
