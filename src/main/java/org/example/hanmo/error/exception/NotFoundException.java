package org.example.hanmo.error.exception;

import org.example.hanmo.error.ErrorCode;

public class NotFoundException extends BusinessException {
  public NotFoundException(String message, ErrorCode errorCode) {
    super(message, errorCode);
  }
}
