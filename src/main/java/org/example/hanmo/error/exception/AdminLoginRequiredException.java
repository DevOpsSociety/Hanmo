package org.example.hanmo.error.exception;

import org.example.hanmo.error.ErrorCode;

public class AdminLoginRequiredException extends BusinessException {
    public AdminLoginRequiredException(String message, ErrorCode errorCode) {
      super(message, errorCode);
    }
}
