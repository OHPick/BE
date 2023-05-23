package com.team11.shareoffice.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team11.shareoffice.global.jwt.JwtUtil;
import com.team11.shareoffice.member.dto.UserInfoDto;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoUserService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Transactional
    public ResponseEntity<ResponseMessage> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = requestAccessToken(code);
        UserInfoDto userInfo = fetchKakaoUserInfo(accessToken);
        String nickname = registerOrUpdateKakaoUser(userInfo);

        String jwtToken = jwtUtil.createKakaoToken(nickname, userInfo.getKakaoId());
        response.addHeader(JwtUtil.ACCESS_TOKEN, jwtToken);

        return ResponseMessage.SuccessResponse("로그인 성공되었습니다.", "");
    }

    private String requestAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", "hIDvbu5u1afjMOo5vFp9tlq7Zaekoh4k")
                .queryParam("redirect_uri", "http://15.164.49.40/oauth/kakao")
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
        String nickname = jsonNode.get("nickname").asText();
        String email = jsonNode.get("email").asText();

        log.info("카카오 사용자 정보: " + id);
        return new UserInfoDto(id,nickname,email);
    }

    private String registerOrUpdateKakaoUser(UserInfoDto kakaoUserInfo) {
        Member member = memberRepository.findByKakaoId(kakaoUserInfo.getKakaoId()).orElse(null);
        String nickname = "";
        if (member == null) {
            nickname = UUID.randomUUID().toString().substring(0, 8);
            memberRepository.save(new Member(kakaoUserInfo.getKakaoId(), nickname, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile1.png", UserRoleEnum.USER));
        } else {
            nickname = member.getNickname();
        }
        return nickname;
    }

    @Transactional
    public ResponseEntity<ResponseMessage> testLogin(String nickname, HttpServletResponse response) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String jwtToken = jwtUtil.createKakaoToken(nickname, member.getKakaoId());
        response.addHeader(JwtUtil.ACCESS_TOKEN, jwtToken);

        return ResponseMessage.SuccessResponse("로그인 성공되었습니다.", member.getNickname());
    }
}