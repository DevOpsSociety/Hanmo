package org.example.hanmo.error.exception;

import org.example.hanmo.error.ErrorCode;

public class AccountDeactivatedException extends BusinessException {

  public AccountDeactivatedException(String message, ErrorCode errorCode) {
    super(message, errorCode);
  }
}
