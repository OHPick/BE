package com.team11.shareoffice.email.controller;

import com.team11.shareoffice.email.dto.CodeRequestDto;
import com.team11.shareoffice.email.dto.EmailRequestDto;
import com.team11.shareoffice.email.service.EmailServiceImpl;
import com.team11.shareoffice.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private final EmailServiceImpl emailService;

    @Operation(summary = "이메일 확인 API")
    @PostMapping("/auth")
    public ResponseDto<?> emailConfirm(@RequestBody EmailRequestDto requestDto) throws Exception {
        return ResponseDto.setSuccess(emailService.sendMessage(requestDto));
    }

    @Operation(summary = "이메일 인증 코드 확인 API")
    @PostMapping("/check")
    public ResponseDto<?> codeCheck(@RequestBody CodeRequestDto codeRequestDto) throws Exception {
        emailService.codeCheck(codeRequestDto);
        return ResponseDto.setSuccess(null);
    }
}
