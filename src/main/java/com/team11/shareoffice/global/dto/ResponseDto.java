package com.team11.shareoffice.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.team11.shareoffice.global.util.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "set")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto <T> {
    @JsonProperty
    private StatusCode status;
    @JsonProperty
    private String message;
    @JsonProperty
    private T data;
    @JsonProperty
    private String errorCode;

    public static <T> ResponseDto<T> setSuccess(T data) {
        return ResponseDto.set(StatusCode.OK, "성공", data, null);
    }

    public static <T> ResponseDto<T> setSuccess(String message, T data) {
        return ResponseDto.set(StatusCode.OK, message, data, null);
    }

    public static <T> ResponseDto<T> setSuccess(String message) {
        return ResponseDto.set(StatusCode.OK, message, null, null);
    }

    public static <T> ResponseDto<T> setBadRequest(String message, String errorCode) {
        return ResponseDto.set(StatusCode.BAD_REQUEST, message, null, errorCode);
    }

    public static <T> ResponseDto<T> setBadRequest(String message, T data, String errorCode) {
        return ResponseDto.set(StatusCode.BAD_REQUEST,  message, data, errorCode);
    }


}
