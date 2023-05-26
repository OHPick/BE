package com.team11.shareoffice.global.exception;

import com.team11.shareoffice.global.util.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final String errorCode;

    public CustomException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode) {
        this(errorCode.getMessage(), errorCode.getErrorCode());
    }


}