package org.example.hanmo.service;

import java.util.List;

import org.example.hanmo.dto.chat.response.ChatMessage;

public interface ChatService {
	void checkAndJoin(String roomId, Long userId);
	List<ChatMessage> loadHistory(String roomId);

	void saveMessage(ChatMessage message);
}
