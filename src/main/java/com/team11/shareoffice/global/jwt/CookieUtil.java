package com.team11.shareoffice.global.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    // 쿠키 생성
    public void createCookie(HttpServletResponse response, String tokenValue) {
        ResponseCookie cookieRefreshToken = ResponseCookie.from(JwtUtil.REFRESH_TOKEN, tokenValue)
                .httpOnly(true)
                .secure(true)
                .sameSite("None") // SameSite 설정
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookieRefreshToken.toString());
    }

    // 쿠키 삭제
    public void createNullCookie(HttpServletRequest request, HttpServletResponse response) {
        ResponseCookie cookieLogoutRefreshToken = ResponseCookie.from("null", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None") // SameSite 설정
                .maxAge(0)
                .path("/")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookieLogoutRefreshToken.toString());
    }

    // 쿠키 찾기
    public String getCookie(HttpServletRequest request, String name) {
        String cookieHeader = request.getHeader("Cookie");

        if (cookieHeader != null) {
            String[] cookieParts = cookieHeader.split(";");
            for (String cookiePart : cookieParts) {
                String[] cookieNameValue = cookiePart.trim().split("=");
                if (cookieNameValue.length == 2) {
                    String cookieName = cookieNameValue[0];
                    String cookieValue = cookieNameValue[1];
                    // Do something with the cookie name and value
                    if (cookieName.equals(name)) {
                        return cookieValue.substring(6);
                    }
                }
            }
        }
        return null;
    }

    // 쿠키 삭제
    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader != null) {
            String[] cookieParts = cookieHeader.split(";");
            for (String cookiePart : cookieParts) {
                String[] cookieNameValue = cookiePart.trim().split("=");
                if (cookieNameValue.length == 2) {
                    String cookieName = cookieNameValue[0];
                    if (cookieName.equals(name)) {
                        ResponseCookie cookieLogoutRefreshToken = ResponseCookie.from(name, "")
                                .httpOnly(true)
                                .secure(true)
                                .sameSite("None") // SameSite 설정
                                .maxAge(0)
                                .path("/")
                                .build();
                        response.addHeader(HttpHeaders.SET_COOKIE, cookieLogoutRefreshToken.toString());
                    }
                }
            }
        }
    }
}
