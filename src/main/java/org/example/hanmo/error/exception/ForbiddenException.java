package org.example.hanmo.error.exception;

import org.example.hanmo.error.ErrorCode;

public class ForbiddenException extends BusinessException {

  public ForbiddenException(String message, ErrorCode errorCode) {
    super(message, errorCode);
  }
}
