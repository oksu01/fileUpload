package com.fileShare.global.exception;

import org.springframework.http.HttpStatus;

public class EmailExistException extends EmailValidationException {
    private static final String CODE = "EMAIL-400";
    private static final String MESSAGE = "이미 존재하는 이메일입니다.";

    public EmailExistException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
