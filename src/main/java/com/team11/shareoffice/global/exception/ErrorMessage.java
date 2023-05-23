package com.team11.shareoffice.global.exception;


import lombok.Getter;

@Getter
public enum ErrorMessage {

    // 400 BAD REQUEST
    ENROLLED_EMAIL("중복된 이메일입니다.", 400),
    ENROLLED_NICKNAME("중복된 닉네임입니다.", 400),
    PASSWORD_MISMATCH("비밀번호를 확인해주세요.", 400),
    OUT_OF_EMAIL_PATTERN("이메일 양식에 맞지 않습니다.", 400),
    OUT_OF_PASSWORD_PATTERN("비밀번호 양식에 맞지 않습니다.", 400),
    UNENROLLED_EMAIL("등록되지 않은 이메일입니다.", 400),

    // 401 Unauthorized
    INVALID_TOKEN("유효하지 않은 토큰입니다.", 401);

    private String message;
    private int statusCode;

    ErrorMessage(String message, int  statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    }
