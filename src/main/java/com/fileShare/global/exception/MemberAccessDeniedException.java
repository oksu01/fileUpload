package com.fileShare.global.exception;

import org.springframework.http.HttpStatus;

public class MemberAccessDeniedException extends MemberException {

    public static final String MESSAGE = "접근 권한이 없습니다.";
    public static final String CODE = "MEMBER-403";

    public MemberAccessDeniedException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
