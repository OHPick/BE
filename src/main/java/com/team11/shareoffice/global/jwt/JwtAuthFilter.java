package com.team11.shareoffice.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team11.shareoffice.global.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final CookieUtil cookieUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtUtil.resolveToken(request, JwtUtil.ACCESS_TOKEN);
        String refreshToken = cookieUtil.getCookie(request,JwtUtil.REFRESH_TOKEN);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (redisService.isBlackListed(accessToken)) {
            log.info("블랙리스트 처리된 토큰");
            jwtExceptionHandler(response, "로그아웃된 아이디 입니다.(블랙리스트)", HttpStatus.UNAUTHORIZED.value());
            return;
        }
        //엑세스 토큰 유효하면 킵고잉
        boolean isValidAccessToken = jwtUtil.validateToken(accessToken);
        if (isValidAccessToken) {
            setAuthentication(jwtUtil.getUserInfoFromToken(accessToken));
            filterChain.doFilter(request, response);
            return;
        }
        //엑세스토큰이 무효한데, 리프레쉬 토큰이 없다면
        if (refreshToken == null) {
            jwtExceptionHandler(response, "엑세스토큰은 무효하고, 리프레쉬토큰이 없습니다.", HttpStatus.UNAUTHORIZED.value());
            cookieUtil.deleteCookie(request,response,JwtUtil.REFRESH_TOKEN);
            return;
        }

        String userEmail = jwtUtil.getUserInfoFromToken(refreshToken);
        if (userEmail == null) {
            jwtExceptionHandler(response, "리프레쉬 토큰이 만료되어 로그인이 필요합니다.", HttpStatus.UNAUTHORIZED.value());
            cookieUtil.deleteCookie(request,response,JwtUtil.REFRESH_TOKEN);
            return;
        }

        //엑세스토큰 무효, 리프레쉬토큰이 있는데 무효하면
        boolean isValidRefreshToken = jwtUtil.validateToken(refreshToken);
        if (isValidRefreshToken) {
            String refreshTokenFromRedis = redisService.getValues(userEmail).substring(6);

            if (!refreshToken.equals(refreshTokenFromRedis)) {
                jwtExceptionHandler(response, "리프레쉬토큰을 확인해주세요. 엑세스토큰을 갱신할 수 없습니다.", HttpStatus.UNAUTHORIZED.value());
                cookieUtil.deleteCookie(request,response,JwtUtil.REFRESH_TOKEN);
                return;
            }
            //리프레쉬 토큰은 유효하면
            String newAccessToken = jwtUtil.createToken(userEmail, JwtUtil.ACCESS_TOKEN);
            response.setHeader(JwtUtil.ACCESS_TOKEN, newAccessToken);
            setAuthentication(jwtUtil.getUserInfoFromToken(newAccessToken.substring(6)));
            log.info("새로운 토큰 생성 완료");
        }

        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String email) {
        Authentication authentication= jwtUtil.createAuthentication(email);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String message, int status) {
        response.setStatus(status);
        response.setContentType("application/json; charset=utf8");
        try {
            String json = new ObjectMapper().writeValueAsString(message);
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
