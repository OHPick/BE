package com.team11.shareoffice.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.member.service.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.team11.shareoffice.global.dto.ResponseDto.setSuccess;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class SocialLoginController {

    private final KakaoService kakaoService;

    // 카카오 로그인
    @Operation(summary = "카카오로그인 API", description = "카카오로그인")
    @GetMapping("/oauth/kakao")
    public ResponseDto<?> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {

        return setSuccess("로그인 성공",  kakaoService.kakaoLogin(code, response));
    }

}