package org.example.hanmo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.example.hanmo.dto.chat.response.ChatMessage;
import org.example.hanmo.service.ChatService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	private String roomKey(String roomId)      { return "chatRoom:"     + roomId; }
	private String historyKey(String roomId)   { return "chatHistory:"  + roomId; }

	@Override
	public void checkAndJoin(String roomId, Long userId) {
		String key = roomKey(roomId);
		if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다.");
		}
		String parts = redisTemplate.opsForValue().get(key);
		if (parts == null || Arrays.stream(parts.split(","))
			.noneMatch(id -> id.equals(String.valueOf(userId)))) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채팅방 참여 권한이 없습니다.");
		}
	}

	@Override
	public void checkParticipant(String roomId, Long userId) {
		checkAndJoin(roomId, userId);
	}

	@Override
	public List<ChatMessage> loadHistory(String roomId) {
		List<String> raw = redisTemplate.opsForList().range(historyKey(roomId), 0, -1);
		if (raw == null) return List.of();
		return raw.stream().map(json -> {
			try {
				return objectMapper.readValue(json, ChatMessage.class);
			} catch (Exception e) {
				throw new RuntimeException("채팅내역 파싱 실패", e);
			}
		}).collect(Collectors.toList());
	}

	@Override
	public void saveMessage(ChatMessage message) {
		String rk = roomKey(message.getRoomId());
		if (!Boolean.TRUE.equals(redisTemplate.hasKey(rk))) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다.");
		}
		try {
			String json = objectMapper.writeValueAsString(message);
			redisTemplate.opsForList().rightPush(historyKey(message.getRoomId()), json);
			Long ttl = redisTemplate.getExpire(rk);
			if (ttl != null && ttl > 0) {
				redisTemplate.expire(historyKey(message.getRoomId()), ttl, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			throw new RuntimeException("메시지 저장 오류", e);
		}
	}
}
