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
	private final ObjectMapper objectMapper;

	@Override
	@Transactional(readOnly = true)
	public void checkAndJoin(String roomId, Long userId) {
		String roomKey = chatDao.keyForRoom(roomId);
		ChatValidate.ensureRoomExists(chatDao.getRedis(), roomKey);
		ChatValidate.ensureUserHasAccess(chatDao.getRedis(), roomKey, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ChatMessage> loadHistory(String roomId) {
		String historyKey = chatDao.keyForHistory(roomId);
		return ChatValidate.parseHistory(chatDao.getRedis(), objectMapper, historyKey);
	}

	@Override
	@Transactional
	public void saveMessage(ChatMessage message) {
		String roomKey    = chatDao.keyForRoom(message.getRoomId());
		String historyKey = chatDao.keyForHistory(message.getRoomId());
		ChatValidate.handleSaveMessage(
			chatDao.getRedis(), objectMapper, message, historyKey, roomKey
		);
	}
}
