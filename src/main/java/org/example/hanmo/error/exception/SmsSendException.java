package org.example.hanmo.error.exception;

import org.example.hanmo.error.ErrorCode;

public class SmsSendException extends BusinessException {
  public SmsSendException(String message, ErrorCode errorCode) {
    super(message, errorCode);
  }
}
