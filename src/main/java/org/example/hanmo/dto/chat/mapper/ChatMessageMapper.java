package org.example.hanmo.dto.chat.mapper;

import org.example.hanmo.dto.chat.request.ChatMessageRequest;
import org.example.hanmo.dto.chat.response.ChatMessage;
import org.example.hanmo.domain.UserEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, ZoneId.class})
public interface ChatMessageMapper {

	@Mapping(target = "roomId",           expression = "java(roomId)")
	@Mapping(target = "senderId", expression = "java(currentUser.getId())")
	@Mapping(target = "senderNickname", expression = "java(currentUser.getNickname())")
	@Mapping(target = "content", source = "request.content")
	@Mapping(target = "sentAt", expression = "java(LocalDateTime.now(ZoneId.of(\"Asia/Seoul\")))")
	ChatMessage toMessage(
		ChatMessageRequest request,
		@Context UserEntity currentUser,
		@Context String roomId
	);
}
