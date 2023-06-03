package com.team11.shareoffice.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team11.shareoffice.global.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JwtAuthFilter.doFilterInternal");

        String accessToken = jwtUtil.resolveToken(request, JwtUtil.ACCESS_TOKEN);
        String refreshToken = jwtUtil.resolveToken(request, JwtUtil.REFRESH_TOKEN);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (redisService.isBlackListed(accessToken)) {
            log.info("블랙리스트 처리된 토큰");
            jwtExceptionHandler(response, "블랙리스트", HttpStatus.UNAUTHORIZED.value());
            return;
        }

        boolean isValidAccessToken = jwtUtil.validateToken(accessToken);
        if (isValidAccessToken) {
            setAuthentication(jwtUtil.getUserInfoFromToken(accessToken));
            filterChain.doFilter(request, response);
            return;
        }

        if (refreshToken == null) {
            jwtExceptionHandler(response, "유효하지 않은 토큰 입니다.", HttpStatus.UNAUTHORIZED.value());
            return;
        }

        String userEmail = jwtUtil.getUserInfoFromToken(refreshToken);
        String refreshTokenFromRedis = redisService.getValues(userEmail).substring(7);

        if (!refreshToken.equals(refreshTokenFromRedis)) {
            jwtExceptionHandler(response, "토큰을 갱신할 수 없습니다.", HttpStatus.UNAUTHORIZED.value());
            return;
        }

        String newAccessToken = jwtUtil.createToken(userEmail, JwtUtil.ACCESS_TOKEN);
        response.setHeader(JwtUtil.ACCESS_TOKEN, newAccessToken);
        setAuthentication(jwtUtil.getUserInfoFromToken(newAccessToken.substring(7)));
        log.info("새로운 토큰 생성 완료");

        filterChain.doFilter(request, response);
    }
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        String accessToken = jwtUtil.resolveToken(request, JwtUtil.ACCESS_TOKEN);
//        String refreshToken = jwtUtil.resolveToken(request, JwtUtil.REFRESH_TOKEN);
//
//        if (accessToken != null) {  //토큰이 있으면 true
//
//            if (redisService.isBlackListed(accessToken)) {  //ATK가 있는데 블랙리스트 일경우(로그아웃한 엑세스 토큰)
//                log.info("블랙리스트 처리된 토큰");
//                jwtExceptionHandler(response, "블랙리스트", HttpStatus.UNAUTHORIZED.value());
//                return;
//
//            } else if (jwtUtil.validateToken(accessToken) || (refreshToken != null)) {  // 1. ATK있고 만료일수도 있고 정상일수도 있음, ATK가 만료라도 RTK라도 뭐가 있다면 tru
//
//                if (jwtUtil.validateToken(accessToken)) {           //2. ATK가 만료가 아니고 유효하면 -> 시큐리티 컨텐스트
//                    setAuthentication(jwtUtil.getUserInfoFromToken(accessToken));
//
//                } else{ //3. 엑세스 토큰이 만료거나 무효한 경우 -> 리프레시 토큰을 확인해서 엑세스토큰 재발급 + 요청 처리
//
//                    String userEmail = jwtUtil.getUserInfoFromToken(refreshToken);
//
//                    String refreshTokenFromRedis = redisService.getValues(userEmail).substring(7);
//
//                    if (refreshToken.equals(refreshTokenFromRedis)) {  //3. ATK가 만료거나 무효한 토큰인데, 리프레쉬토큰은 redisDB에 유효하면 -> ATK 재발급
//                        // new accessToken 발급
//                        String newAccessToken = jwtUtil.createToken(userEmail, JwtUtil.ACCESS_TOKEN);
//                        // 헤더에 새로운 Access 토큰 넣기
//                        response.setHeader(JwtUtil.ACCESS_TOKEN, newAccessToken);
//                        // Security context에 인증 정보 저장
//                        String newToken = newAccessToken.substring(7);
//                        setAuthentication(jwtUtil.getUserInfoFromToken(newToken));
//
//                        log.info("새로운 토큰 생성 완료");
//                    }
//                }
//
//            } else {   //4. Access & Refresh 토큰 둘다 만료시
//                log.info("토큰 에러 here");
//                jwtExceptionHandler(response, "유효하지 않은 토큰 입니다.", HttpStatus.UNAUTHORIZED.value());
//                return;
//            }
//        }
//        filterChain.doFilter(request, response);
//    }


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
