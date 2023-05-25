package com.team11.shareoffice.global.jwt;

import com.team11.shareoffice.global.jwt.dto.TokenDto;
import com.team11.shareoffice.global.jwt.entity.RefreshToken;
import com.team11.shareoffice.global.jwt.repository.RefreshTokenRepository;
import com.team11.shareoffice.global.security.UserDetailsServiceImpl;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsServiceImpl userDetailsService;
    public static final String ACCESS_TOKEN = "Access_Token";
    public static final String REFRESH_TOKEN = "Refresh_Token";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L;   //AccessToken Time 1 hr
    private static final long REFRESH_TOKEN_TIME = 24 * 60 * 60 * 100L; //RefreshToken Time 1 day

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오기
    public String resolveToken(HttpServletRequest request, String type) {
        if (type.equals(ACCESS_TOKEN)) {
            String bearerToken = request.getHeader(ACCESS_TOKEN);
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
                return bearerToken.substring(7);
            }
            return null;
        } else {
            String bearerToken = request.getHeader(REFRESH_TOKEN);
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
                return bearerToken.substring(7);
            }
            return null;
        }
    }

    public TokenDto createAllToken(String userEmail) {
        return new TokenDto(createToken(userEmail, ACCESS_TOKEN), createToken(userEmail, REFRESH_TOKEN));
    }

    // 토큰 생성
    public String createToken(String userEmail, String token) {
        Date date = new Date();
        long time = token.equals(ACCESS_TOKEN) ? ACCESS_TOKEN_TIME : REFRESH_TOKEN_TIME;

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(userEmail)
                        .setExpiration(new Date(date.getTime() + time))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    //RefreshToken 검증
    //DB에 저장돼 있는 토큰과 비교
    public Boolean validateRefreshToken(String token) {
        //1차 토큰 검증
        if (!validateToken(token)) return false;

        // 이 부분 오료 해결시 주석 푸시면 됩니다~
        //사용자 찾기
        Member member = memberRepository.findByEmail(getUserInfoFromToken(token)).orElseThrow(
                () -> new NullPointerException(HttpStatus.BAD_REQUEST.getReasonPhrase())
        );
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMember(member);

        // 사용자의 Refresh 토큰 가져오기
        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken().substring(7));

    }

    // 카카오토큰 생성
//    public String createKakaoToken(String userEmail, Long kakaoId) {
//        Date date = new Date();
//
//        return BEARER_PREFIX +
//                Jwts.builder()
//                        .setSubject(kakaoId.toString())
//                        .claim(ACCESS_TOKEN, userEmail)
//                        .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME))
//                        .setIssuedAt(date)
//                        .signWith(key, signatureAlgorithm)
//                        .compact();
//    }

    // 토큰에서 사용자 정보 가져오기
    public String getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    // 인증 객체 생성
    @Transactional(readOnly = true)
    public Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}


