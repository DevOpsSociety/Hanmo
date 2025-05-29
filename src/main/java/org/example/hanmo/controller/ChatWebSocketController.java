package org.example.hanmo.controller;

import org.example.hanmo.dto.chat.response.ChatMessage;
import org.example.hanmo.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
	private final ChatService chatService;

	private final SimpMessagingTemplate messagingTemplate;
	@MessageMapping("/chat.send/{roomId}")
	public void onMessage(@DestinationVariable String roomId, ChatMessage msg) {
		chatService.checkAndJoin(roomId, msg.getSenderId());
		chatService.saveMessage(msg);
		messagingTemplate.convertAndSend("/topic/chat/" + roomId, msg);
	}
}
