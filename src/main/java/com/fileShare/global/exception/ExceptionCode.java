package com.fileShare.global.exception;

import lombok.Getter;


public enum ExceptionCode {

    NOT_FOUND(404, "요청하신 데이터를 찾을 수 없습니다."),
    NOT_FOUND_ACCOUNT(400, "Account를 찾을 수 없습니다."),
    EMAIL_EXIST(409, "이미 회원가입된 Email입니다."),
    NICKNAME_EXIST(409, "이미 존재하는 닉네임입니다."),
    NOT_FOUND_QUESTION(400, "Question을 찾을 수 없습니다."),
    NON_ACCESS_MODIFY(400, "수정 권한이 없습니다."),
    ILLEGAL_FILENAME(400, "잘못된 형식의 Filename 입니다."),
    NOT_FOUND_ANSWER(400, "Answer를 찾을 수 없습니다."),
    DUPLICATED_SELECT(400, "이미 채택된 Answer가 있습니다"),
    ACCESS_TOKEN_EXPIRATION(400, "로그인이 필요한 기능입니다."),
    LOGIN_FAILURE(401, "이메일, 비밀번호가 틀렸습니다."),
    NON_ACCESS(403, "자신에게는 리뷰 등록을 할 수 없습니다."),
    TOKEN_EXPIRATION(402, "토큰이 만료되어 로그인이 필요합니다."),
    ACCESS_DENIED(403, "유효한 토큰이 아닙니다.");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
