package com.team11.shareoffice.global.util;

import lombok.Getter;

@Getter
public enum StatusCode {
    OK(200, "OK"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    NOT_FOUND(404, "NOT_FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");

    int statusCode;
    String code;

    StatusCode(int statusCode, String code) {
        this.statusCode = statusCode;
        this.code = code;
    }
}