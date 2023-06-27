package com.team11.shareoffice.global.jwt.controller;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.jwt.CookieUtil;
import com.team11.shareoffice.global.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TokenController {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Operation(summary = "토큰 재발급 API", description = "토큰 재발급")
    @PostMapping("/reissue")
    public ResponseDto<?> signup(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = cookieUtil.getCookie(request,JwtUtil.REFRESH_TOKEN);
        if(refreshToken == null){
            cookieUtil.createNullCookie(response);
            return ResponseDto.setBadRequest("리프레쉬 토큰이 만료되어 로그인이 필요합니다.", HttpStatus.UNAUTHORIZED.toString());
        }
        return ResponseDto.setSuccess("토큰 재 생성!",jwtUtil.reissueToken(refreshToken));
    }
}
