package org.example.hanmo.util;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.chat.response.ChatRoomInfoResponse;
import org.example.hanmo.vaildate.ChatValidate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatRoomUtil {

	private final StringRedisTemplate stringRedisTemplate;

	public void createChatRoom(Long roomId, List<Long> userIds, Duration duration) {
		String roomKey = "chatRoom:" + roomId;
		String participants = String.join(",", userIds.stream().map(String::valueOf).toList());
		Map<String, String> meta = new HashMap<>();
		meta.put("participants", participants);
		String createdAtKorea = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
			.truncatedTo(ChronoUnit.MINUTES)
			.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		meta.put("createdAt", createdAtKorea);
		meta.put("participantCount", String.valueOf(userIds.size()));
		stringRedisTemplate.opsForHash().putAll(roomKey, meta);
		stringRedisTemplate.expire(roomKey, duration);
		for (Long userId : userIds) {
			String userRoomKey = "userRoom:" + userId;
			stringRedisTemplate.opsForValue().set(userRoomKey, String.valueOf(roomId), duration);
		}
	}

	public ChatRoomInfoResponse getChatRoomInfo(Long userId) {
		String roomIdStr = ChatValidate.getRoomIdByUser(stringRedisTemplate, userId);
		String roomKey = "chatRoom:" + roomIdStr;
		ChatValidate.ensureRoomExists(stringRedisTemplate, roomKey);
		String createdAt = ChatValidate.getCreatedAt(stringRedisTemplate, roomKey);
		int participantCount = ChatValidate.getParticipantCount(stringRedisTemplate, roomKey);

		return new ChatRoomInfoResponse(Long.valueOf(roomIdStr), createdAt, participantCount);
	}
}
