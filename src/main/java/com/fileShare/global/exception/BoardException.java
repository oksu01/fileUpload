package com.fileShare.global.exception;

import org.springframework.http.HttpStatus;

public class BoardException extends BusinessException  {
    protected BoardException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
