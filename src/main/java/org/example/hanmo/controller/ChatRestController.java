package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.chat.request.ChatMessageRequest;
import org.example.hanmo.dto.chat.response.ChatMessage;
import org.example.hanmo.repository.user.UserRepository;
import org.example.hanmo.service.ChatService;
import org.example.hanmo.vaildate.AuthValidate;
import org.example.hanmo.vaildate.UserValidate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {
	private final ChatService chatService;
	private final AuthValidate authValidate;
	private final SimpMessagingTemplate messagingTemplate;
	private final UserValidate userValidate;
	private final UserRepository userRepository;
	private String getTempToken(HttpServletRequest request) {
		String tempToken = request.getHeader("tempToken");
		if (tempToken == null) {
			tempToken = request.getParameter("tempToken");
		}
		return tempToken;
	}

	@Operation(summary = "채팅방 참여", tags = {"채팅"})
	@PostMapping("/rooms/{roomId}/join")
	public ResponseEntity<Void> joinRoom(@PathVariable String roomId, HttpServletRequest request) {
		String tempToken = getTempToken(request);
		Long userId = authValidate.validateTempToken(tempToken).getId();
		chatService.checkAndJoin(roomId, userId);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "채팅방 메시지 기록 조회", tags = {"채팅"})
	@GetMapping("/rooms/{roomId}/get-history")
	public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable String roomId, HttpServletRequest request) {
		String tempToken = getTempToken(request);
		Long userId = authValidate.validateTempToken(tempToken).getId();
		chatService.checkAndJoin(roomId, userId);
		List<ChatMessage> history = chatService.loadHistory(roomId);
		return ResponseEntity.ok(history);
	}

	@Operation(summary = "채팅방 메시지 전송", tags = {"채팅"})
	@PostMapping("/rooms/{roomId}/send-message")
	public ResponseEntity<ChatMessage> sendMessage(@PathVariable String roomId, HttpServletRequest request, @RequestBody ChatMessageRequest req) {
		String tempToken = getTempToken(request);
		Long userId = authValidate.validateTempToken(tempToken).getId();
		String nickname = userValidate.getUserById(userId, userRepository).getNickname();

		ChatMessage msg = new ChatMessage();
		msg.setRoomId(roomId);
		msg.setSenderId(userId);
		msg.setSenderNickname(nickname);
		msg.setContent(req.getContent());
		msg.setSentAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));

		chatService.saveMessage(msg);
		messagingTemplate.convertAndSend("/topic/chat/" + roomId, msg);

		return ResponseEntity.ok(msg);
	}
}
