package com.team11.shareoffice.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.jwt.JwtUtil;
import com.team11.shareoffice.member.dto.UserInfoDto;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public ResponseDto<?> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {

        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);
        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        UserInfoDto userInfo = fetchKakaoUserInfo(accessToken);
        // 3. 필요 시에 회원 가입
        //Member kakaoMember = registerKakaoUserIfNeeded(userInfo);

        String nickname = registerOrUpdateKakaoUser(userInfo);

        String jwtToken = jwtUtil.createKakaoToken(nickname, userInfo.getKakaoId());
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtToken);

        return ResponseDto.setSuccess("로그인에 성공했습니다");

    }

    private String getToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", "a2e918a8313b1ec2a828afcfa8e8991b")
                .queryParam("redirect_uri", "http://localhost:8080/oauth/kakao")
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
        String nickname =jsonNode.get("kakao_account").get("email").asText();
        String email = jsonNode.get("properties").get("nickname").asText();

        log.info("카카오 사용자 정보: " + id);
        return new UserInfoDto(id,nickname,email);
    }

    private String registerOrUpdateKakaoUser(UserInfoDto userInfo) {
        Member member = memberRepository.findByKakaoId(userInfo.getKakaoId()).orElse(null);
        String nickname = "";

        if (member == null) {
            nickname = UUID.randomUUID().toString().substring(0, 8);

            member = new Member(userInfo.getKakaoId(), nickname, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile1.png");
            memberRepository.save(member);

        } else {
            nickname = member.getNickname();
        }
        return nickname;
    }

}