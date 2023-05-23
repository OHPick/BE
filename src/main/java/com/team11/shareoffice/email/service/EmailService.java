package com.team11.shareoffice.email.service;

import com.team11.shareoffice.email.dto.CodeRequestDto;
import com.team11.shareoffice.email.dto.EmailRequestDto;
import com.team11.shareoffice.global.dto.ResponseDto;

public interface EmailService {
    ResponseDto<?> sendMessage(EmailRequestDto requestDto)throws Exception;
    ResponseDto<?> codeCheck(CodeRequestDto requestDto)throws Exception;
}
