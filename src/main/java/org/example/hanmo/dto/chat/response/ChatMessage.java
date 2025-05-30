package org.example.hanmo.dto.chat.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
	private String roomId;
	private Long senderId;
	private String senderNickname;
	private String content;
	private LocalDateTime sentAt;
}
