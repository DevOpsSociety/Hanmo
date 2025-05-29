package org.example.hanmo.error.exception;

import org.example.hanmo.error.ErrorCode;

public class ChatServiceException extends BusinessException {
	public ChatServiceException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
