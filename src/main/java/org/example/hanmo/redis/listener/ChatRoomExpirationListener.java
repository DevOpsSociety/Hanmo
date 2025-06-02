package org.example.hanmo.redis.listener;


import java.nio.charset.StandardCharsets;
import java.util.List;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.repository.user.UserRepository;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ChatRoomExpirationListener extends KeyExpirationEventMessageListener {

	private final SimpMessagingTemplate messaging;
	private final UserRepository userRepository;

	public ChatRoomExpirationListener(
		RedisMessageListenerContainer container,
		SimpMessagingTemplate messaging,
		UserRepository userRepository
	) {
		super(container);
		this.messaging = messaging;
		this.userRepository = userRepository;
	}

	@Transactional
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = new String(message.getBody(), StandardCharsets.UTF_8);
		if (!expiredKey.startsWith("chatRoom:")) {
			return;
		}

		String roomIdStr = expiredKey.substring("chatRoom:".length());
		messaging.convertAndSend(
			"/topic/chat/" + roomIdStr + "/close",
			"채팅방이 만료되어 종료됩니다."
		);
		Long roomId = Long.valueOf(roomIdStr);
		List<UserEntity> participants =
			userRepository.findAllByGroupId(roomId);

		for (UserEntity u : participants) {
			u.setGenderMatchingType(null);
			u.setMatchingType(null);
			u.setUserStatus(null);
			u.setMatchingGroup(null);
			userRepository.save(u);
		}
	}
}
