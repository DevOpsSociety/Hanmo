package org.example.hanmo.error.exception;

import org.example.hanmo.error.ErrorCode;

public class TempTokenException extends BusinessException {
    public TempTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
