package com.team11.shareoffice.global.jwt.controller;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.jwt.CookieUtil;
import com.team11.shareoffice.global.jwt.JwtUtil;
import com.team11.shareoffice.global.util.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = cookieUtil.getCookie(request,JwtUtil.REFRESH_TOKEN);
        if(refreshToken == null){
            cookieUtil.createNullCookie(response);
            throw new CustomException(ErrorCode.NEED_LOGIN);
        }

        return ResponseDto.setSuccess("토큰 재 생성!",jwtUtil.reissueToken(refreshToken));
    }
}
