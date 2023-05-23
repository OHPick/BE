package com.team11.shareoffice.global.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team11.shareoffice.global.util.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "set")
public class ResponseDto <T> {
    @JsonProperty
    private StatusCode status;
    @JsonProperty
    private String message;
    @JsonProperty
    private T data;

    public static <T> ResponseDto<T> setSuccess(T data) {
        return ResponseDto.set(StatusCode.OK, "success", data);
    }

    public static <T> ResponseDto<T> setSuccess(String message, T data) {
        return ResponseDto.set(StatusCode.OK, message, data);
    }

    public static <T> ResponseDto<T> setBadRequest(String message) {
        return ResponseDto.set(StatusCode.BAD_REQUEST, message, null);
    }

    public static <T> ResponseDto<T> setBadRequest(String message, T data) {
        return ResponseDto.set(StatusCode.BAD_REQUEST, message, data);
    }

}
