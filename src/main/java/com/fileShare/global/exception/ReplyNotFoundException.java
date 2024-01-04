package com.fileShare.global.exception;

import org.springframework.http.HttpStatus;

public class ReplyNotFoundException extends ReplyException {
    private static final String CODE = "REPLY-404";
    private static final String MESSAGE = "댓글이 존재하지 않습니다.";

    public ReplyNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
