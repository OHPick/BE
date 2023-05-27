package com.team11.shareoffice.global.exception;

import com.team11.shareoffice.global.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ResponseDto> handleCustomException(CustomException e) {

        ResponseDto<Object> objectResponseDto = ResponseDto.setBadRequest(e.getMessage(),e.getErrorCode());
        return new ResponseEntity<>(objectResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ResponseDto> handleRuntimeException(Exception e) {

        ResponseDto<Object> objectResponseDto = ResponseDto.setBadRequest(e.getMessage(),"Error");
        return new ResponseEntity<>(objectResponseDto, HttpStatus.BAD_REQUEST);
    }
}
