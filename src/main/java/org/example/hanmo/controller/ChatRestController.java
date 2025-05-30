// src/main/java/org/example/hanmo/controller/ChatRestController.java
package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.annotation.CurrentUser;
import org.example.hanmo.annotation.LoginRequired;
import org.example.hanmo.dto.chat.mapper.ChatMessageMapper;
import org.example.hanmo.dto.chat.request.ChatMessageRequest;
import org.example.hanmo.dto.chat.response.ChatMessage;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.service.ChatService;
import org.example.hanmo.util.ChatRoomUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@LoginRequired
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRestController {
	private final ChatService chatService;
	private final ChatRoomUtil chatRoomUtil;
	private final SimpMessagingTemplate messagingTemplate;
	private final ChatMessageMapper chatMessageMapper;

	@Operation(summary = "채팅방 참여", tags = {"채팅"})
	@PostMapping("/rooms/{roomId}/join")
	public ResponseEntity<String> enterChatRoom(@PathVariable String roomId, @CurrentUser UserEntity currentUser) {
		chatService.checkAndJoin(roomId, currentUser.getId());
		return ResponseEntity.ok("채팅방에 입장하셨습니다.");
	}

	@Operation(summary = "채팅방 메시지 기록 조회", tags = {"채팅"})
	@GetMapping("/rooms/{roomId}/get-history")
	public ResponseEntity<List<ChatMessage>> fetchChatHistory(@PathVariable String roomId, @CurrentUser UserEntity currentUser) {
		chatService.checkAndJoin(roomId, currentUser.getId());
		List<ChatMessage> history = chatService.loadHistory(roomId);
		return ResponseEntity.ok(history);
	}

	@Operation(summary = "채팅방 메시지 전송", tags = {"채팅"})
	@PostMapping("/rooms/{roomId}/send-message")
	public ResponseEntity<ChatMessage> postChatMessage(@PathVariable String roomId, @CurrentUser UserEntity currentUser, @RequestBody ChatMessageRequest req) {
		ChatMessage msg = chatMessageMapper.toMessage(req, currentUser, roomId);
		chatService.saveMessage(msg);
		messagingTemplate.convertAndSend("/topic/chat/" + roomId, msg);
		return ResponseEntity.ok(msg);
	}

	@Operation(summary = "내 채팅방 번호 조회", tags = {"채팅"})
	@GetMapping("/rooms/my-room")
	public ResponseEntity<Long> fetchMyChatRoomId(@CurrentUser UserEntity currentUser) {
		Long roomId = chatRoomUtil.findRoomByUserId(currentUser.getId()).orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
		return ResponseEntity.ok(roomId);
	}
}
