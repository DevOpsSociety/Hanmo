package org.example.hanmo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatRoomUtil {

	private final StringRedisTemplate stringRedisTemplate;

	public void createChatRoom(Long roomId, List<Long> userIds, Duration duration) {
		String chatRoomKey = "chatRoom:" + roomId;
		String participants = String.join(",",
			userIds.stream().map(String::valueOf).toList()
		);
		stringRedisTemplate.opsForValue().set(chatRoomKey, participants, duration);
		for (Long userId : userIds) {
			String userRoomKey = "userRoom:" + userId;
			stringRedisTemplate.opsForValue().set(userRoomKey, String.valueOf(roomId), duration);
		}
	}

	/** (선택) 유저가 속한 방 조회 헬퍼 */
	public Optional<Long> findRoomByUserId(Long userId) {
		String roomId = stringRedisTemplate.opsForValue().get("userRoom:" + userId);
		return roomId != null ? Optional.of(Long.valueOf(roomId)) : Optional.empty();
	}}
