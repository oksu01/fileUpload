package com.fileShare.global.exception;

import org.springframework.http.HttpStatus;

public class BoardNotFoundException extends BoardException {
    private static final String CODE = "BOARD-404";
    private static final String MESSAGE = "존재하지않는 게시판입니다.";

    public BoardNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}

