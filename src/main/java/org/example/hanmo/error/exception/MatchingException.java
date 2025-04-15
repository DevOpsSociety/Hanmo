package org.example.hanmo.error.exception;

import org.example.hanmo.error.ErrorCode;

public class MatchingException extends BusinessException {
  public MatchingException(String message, ErrorCode errorCode) {
    super(message, errorCode);
  }
}
