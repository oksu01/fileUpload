package com.fileShare.global.exception;

import org.springframework.http.HttpStatus;

public class MemberPasswordNotMismatchException extends MemberException {

    private static final String CODE = "PASSWORD-400";
    private static final String MESSAGE = "비밀번호가 일치하지 않습니다";

    public MemberPasswordNotMismatchException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
