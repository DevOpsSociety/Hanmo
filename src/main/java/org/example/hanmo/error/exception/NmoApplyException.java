package org.example.hanmo.error.exception;

import org.example.hanmo.error.ErrorCode;

public class NmoApplyException extends BusinessException {
  public NmoApplyException(String message, ErrorCode errorCode) {
    super(message, errorCode);
  }
}
