package com.fileShare.global.exception;

import org.springframework.http.HttpStatus;

public class FileException extends BusinessException  {
    protected FileException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
