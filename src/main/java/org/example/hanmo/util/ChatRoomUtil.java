package org.example.hanmo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatRoomUtil {

	private final StringRedisTemplate stringRedisTemplate;

	public void createChatRoom(Long groupId, List<Long> userIds, Duration duration) {
		String chatRoomKey = "chatRoom:" + groupId;
		String participants = String.join(",",
			userIds.stream().map(String::valueOf).toList()
		);
		stringRedisTemplate.opsForValue().set(chatRoomKey, participants, duration);
	}
}
