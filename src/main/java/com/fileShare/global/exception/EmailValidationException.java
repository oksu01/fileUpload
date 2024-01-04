package com.fileShare.global.exception;

import org.springframework.http.HttpStatus;

public class EmailValidationException extends BusinessException {

    protected EmailValidationException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}