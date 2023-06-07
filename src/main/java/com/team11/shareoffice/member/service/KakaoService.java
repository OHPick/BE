package com.team11.shareoffice.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.jwt.JwtUtil;
import com.team11.shareoffice.global.jwt.dto.TokenDto;
import com.team11.shareoffice.global.service.RedisService;
import com.team11.shareoffice.member.dto.UserInfoDto;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.UUID;

import static com.team11.shareoffice.global.dto.ResponseDto.setSuccess;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KakaoService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
//    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisService redisService;


    @Value("${kakao.client.secret}")
    private String clientSecret;

    public ResponseDto<?> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {

        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);
        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        UserInfoDto userInfo = fetchKakaoUserInfo(accessToken);
        // 3. 필요 시에 회원 가입
        Member kakaoUser = registerKakaoUserIfNeeded(userInfo);
//        // 4. jWT 액서스 토큰 반환
//        String createToken = jwtUtil.createToken(kakaoUser.getEmail(), jwtUtil.ACCESS_TOKEN);
//        //Token 생성
//        TokenDto tokenDto = jwtUtil.createAllToken(userInfo.getEmail());
//        //RefreshToken 있는지 확인
//        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMember(kakaoUser);  //이부분 수정해야할듯
//        // 있으면 새 토큰 발급 후 업데이트
//        // 없으면 새로 만들고 DB에 저장
//        if (refreshToken.isPresent()) {
//            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
//        } else {
//            refreshTokenRepository.saveAndFlush(RefreshToken.saveToken(tokenDto.getRefreshToken(), kakaoUser));
//        }
//        //header에 accesstoken, refreshtoken 추가
//        response.addHeader(JwtUtil.ACCESS_TOKEN, createToken);
//        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
//        return ResponseDto.setSuccess("로그인에 성공했습니다");

        issueTokens(response, kakaoUser.getEmail());

        return setSuccess("로그인 성공");
    }

    public void issueTokens(HttpServletResponse response, String email){
        TokenDto tokenDto = jwtUtil.createAllToken(email);
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());

        redisService.setValues(email, tokenDto.getRefreshToken(), Duration.ofDays(1));
    }

    private String getToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", "a2e918a8313b1ec2a828afcfa8e8991b")
                //.queryParam("redirect_uri", "http://localhost:3000/oauth/kakao")
                .queryParam("redirect_uri", "https://ohpick.vercel.app/oauth/kakao")
                .queryParam("Client_Secret", clientSecret)
                .queryParam("code", code)
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    /////////////
    private UserInfoDto fetchKakaoUserInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://kapi.kakao.com/v2/user/me";

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        String email =jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties").get("nickname").asText();

        log.info("카카오 사용자 정보: " + id);
        return new UserInfoDto(id,nickname,email);
    }

    //////////////////////////////////////////////////////////////
    private Member registerKakaoUserIfNeeded(UserInfoDto userInfo) {
        // DB에 중복된 kakaoId 있는지 확인
        Member kakaomember = memberRepository.findByKakaoId(userInfo.getKakaoId()).orElse(null);

        if (kakaomember == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoEmail = userInfo.getEmail();
            Member sameEmailUser = memberRepository.findByEmail(kakaoEmail).orElse(null);
            if (sameEmailUser != null) {
                kakaomember = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaomember = kakaomember.kakaoIdUpdate(userInfo.getKakaoId());
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                kakaomember = new Member(userInfo.getEmail(), userInfo.getKakaoId(), encodedPassword, userInfo.getNickname());
            }

            memberRepository.save(kakaomember);

        }

        return kakaomember;
    }

}
