package org.example.hanmo.error.exception;


import org.example.hanmo.error.ErrorCode;

public class UnAuthorizedException extends BusinessException{

    public UnAuthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
