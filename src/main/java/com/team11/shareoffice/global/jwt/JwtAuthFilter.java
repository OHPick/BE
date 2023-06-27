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
    private final CookieUtil cookieUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtUtil.resolveToken(request, JwtUtil.ACCESS_TOKEN);
        String refreshToken = cookieUtil.getCookie(request,JwtUtil.REFRESH_TOKEN);
        String servletPath = request.getServletPath();
        String servletMethod = request.getMethod();

        // 토큰이 만료되어도 메인 페이지는 보여준다.
        if(servletPath.equals("/api/posts") && servletMethod.equals("GET")){
            filterChain.doFilter(request, response);
            return;
        }
        //토큰 재성성 api
        if(servletPath.equals("/api/reissue")){
            filterChain.doFilter(request, response);
            return;
        }

        if (accessToken != null) {
            //로그아웃된 엑세스토큰인 경우
            if (redisService.isBlackListed(accessToken)) {
                jwtExceptionHandler(response, "로그아웃된 아이디 입니다.(블랙리스트)", HttpStatus.UNAUTHORIZED.value());
                return;
            }

            //엑세스 토큰 유효하wl 않으면 킵고잉
            boolean isValidAccessToken = jwtUtil.validateToken(accessToken);
            if (!isValidAccessToken) {
                //엑세스토큰이 무효한데, 리프레쉬 토큰이 없다면
                if (refreshToken == null) {
                    jwtExceptionHandler(response, "엑세스토큰은 무효하고, 리프레쉬토큰이 없습니다.", HttpStatus.UNAUTHORIZED.value());
                    cookieUtil.createNullCookie(response);
                    return;
                }
                //엑세스만료 리프레쉬 유효하다면 레디스에서 확인후 갱신. 레디스와 다르면 로그아웃 처리.
                boolean isValidRefreshToken = jwtUtil.validateToken(refreshToken);
                if (isValidRefreshToken) {
                    String userEmail = jwtUtil.getUserInfoFromToken(refreshToken);
                    String refreshTokenFromRedis = redisService.getValues(userEmail).substring(6);

                    if (!refreshToken.equals(refreshTokenFromRedis)) {
                        jwtExceptionHandler(response, "리프레쉬토큰을 확인해주세요. 엑세스토큰을 갱신할 수 없습니다.", HttpStatus.UNAUTHORIZED.value());
                        cookieUtil.deleteCookie(request, response, JwtUtil.REFRESH_TOKEN);
                        return;
                    }
                    //리프레쉬 토큰은 유효하면
                    // TOKEN_ERROR 메세지를 반환하여 클라이언트에서 reissue api로 연결
                    jwtExceptionHandler(response, "TOKEN_ERROR", HttpStatus.UNAUTHORIZED.value());
                    return;
                }
            }
            setAuthentication(jwtUtil.getUserInfoFromToken(accessToken));
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
