package com.team11.shareoffice.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team11.shareoffice.member.dto.UserInfoDto;
import com.team11.shareoffice.member.service.KakaoUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SocialLoginController {
    private final KakaoUserService kakaoUserService;

    // 카카오 로그인
    @GetMapping("/api/kakao/callback")
    public ResponseEntity<ResponseMessage> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoUserService.kakaoLogin(code, response);
    }
}