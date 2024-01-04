package com.fileShare.global.exception;

import org.springframework.http.HttpStatus;

public class FileNotFoundException extends FileException {
    private static final String CODE = "File-404";
    private static final String MESSAGE = "존재하지않는 파일입니다.";

    public FileNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
