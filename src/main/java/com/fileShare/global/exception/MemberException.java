package com.fileShare.global.exception;

import org.springframework.http.HttpStatus;

public abstract class MemberException extends BusinessException {

    protected MemberException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
