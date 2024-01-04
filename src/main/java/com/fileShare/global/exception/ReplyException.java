package com.fileShare.global.exception;

import org.springframework.http.HttpStatus;

public abstract class ReplyException extends BusinessException {

    protected ReplyException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
