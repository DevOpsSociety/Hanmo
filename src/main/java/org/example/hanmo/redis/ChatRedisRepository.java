package org.example.hanmo.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hanmo.dto.chat.response.ChatMessage;
import org.example.hanmo.vaildate.ChatValidate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatRedisRepository {
	private final StringRedisTemplate redis;
	private final ObjectMapper        mapper;
	private static final String ROOM    = "chatRoom:";
	private static final String HISTORY = "chatHistory:";

	public ChatRedisRepository(StringRedisTemplate redis, ObjectMapper mapper) {
		this.redis  = redis;
		this.mapper = mapper;
	}

	/** 서비스에서 유효성 검사 유틸로 넘기기 위해 getter 추가 */
	public StringRedisTemplate getRedis() {
		return redis;
	}

	public String keyForRoom(String roomId) {
		return ROOM + roomId;
	}

	public String keyForHistory(String roomId) {
		return HISTORY + roomId;
	}

	public boolean existsRoom(String roomId) {
		return Boolean.TRUE.equals(redis.hasKey(keyForRoom(roomId)));
	}

	public boolean hasUser(String roomId, Long userId) {
		String participants = redis.opsForValue().get(keyForRoom(roomId));
		return participants != null && participants.contains(String.valueOf(userId));
	}

	public List<ChatMessage> findHistory(String roomId) {
		return ChatValidate.parseHistory(redis, mapper, keyForHistory(roomId));
	}

	public void saveMessage(ChatMessage msg) {
		String roomKey    = keyForRoom(msg.getRoomId());
		String historyKey = keyForHistory(msg.getRoomId());
		ChatValidate.handleSaveMessage(redis, mapper, msg, historyKey, roomKey);
	}
}
