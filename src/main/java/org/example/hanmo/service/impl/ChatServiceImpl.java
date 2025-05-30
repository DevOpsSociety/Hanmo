package org.example.hanmo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.chat.response.ChatMessage;
import org.example.hanmo.redis.ChatRedisRepository;
import org.example.hanmo.service.ChatService;
import org.example.hanmo.vaildate.ChatValidate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

	private final ChatRedisRepository chatDao;
	private final ObjectMapper          objectMapper;

	@Override
	@Transactional(readOnly = true)
	public void checkAndJoin(String roomId, Long userId) {
		String roomKey = chatDao.keyForRoom(roomId);
		// 1) 방 존재 검증
		ChatValidate.ensureRoomExists(chatDao.getRedis(), roomKey);
		// 2) 사용자 접근 권한 검증
		ChatValidate.ensureUserHasAccess(chatDao.getRedis(), roomKey, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ChatMessage> loadHistory(String roomId) {
		String historyKey = chatDao.keyForHistory(roomId);
		// Redis에서 raw JSON 꺼내 ChatMessage 객체로 파싱
		return ChatValidate.parseHistory(chatDao.getRedis(), objectMapper, historyKey);
	}

	@Override
	@Transactional
	public void saveMessage(ChatMessage message) {
		String roomKey    = chatDao.keyForRoom(message.getRoomId());
		String historyKey = chatDao.keyForHistory(message.getRoomId());
		// 메시지 직렬화·저장·TTL 갱신까지 유틸에서 처리
		ChatValidate.handleSaveMessage(
			chatDao.getRedis(), objectMapper, message, historyKey, roomKey
		);
	}
}
