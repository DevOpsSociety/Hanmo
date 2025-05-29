package org.example.hanmo.redis.listener;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ChatRoomExpirationListener {

	private final RedisMessageListenerContainer container;
	private final SimpMessagingTemplate messaging;

	@PostConstruct
	public void init() {
		container.addMessageListener(
			new MessageListenerAdapter(this, "onMessage"),
			new ChannelTopic("__keyevent@0__:expired")
		);
	}

	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = message.toString();
		if (expiredKey.startsWith("chatRoom:")) {
			String roomId = expiredKey.substring("chatRoom:".length());
			messaging.convertAndSend(
				"/topic/chat/" + roomId + "/close",
				"채팅방이 만료되어 종료됩니다."
			);
		}
	}
}
