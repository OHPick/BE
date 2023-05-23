package com.team11.shareoffice.email.controller;

import com.team11.shareoffice.email.dto.CodeRequestDto;
import com.team11.shareoffice.email.dto.EmailRequestDto;
import com.team11.shareoffice.email.service.EmailService;
import com.team11.shareoffice.email.service.EmailServiceImpl;
import com.team11.shareoffice.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private final EmailServiceImpl emailService;
    @PostMapping("/auth")
    public ResponseDto<?> emailConfirm(@RequestBody EmailRequestDto requestDto) throws Exception {

        return emailService.sendMessage(requestDto);
    }


    @PostMapping("/check")
    public ResponseDto<?> codeCheck(@RequestBody CodeRequestDto codeRequestDto) throws Exception {
        return emailService.codeCheck(codeRequestDto);
    }
}
