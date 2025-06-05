package org.example.hanmo.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomInfoResponse {
	private Long roomId;
	private String createdAt;
	private int participantCount;
}

