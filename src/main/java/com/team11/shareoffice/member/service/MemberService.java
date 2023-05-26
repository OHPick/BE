package com.team11.shareoffice.member.service;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.jwt.JwtUtil;
import com.team11.shareoffice.global.jwt.dto.TokenDto;
import com.team11.shareoffice.global.jwt.entity.RefreshToken;
import com.team11.shareoffice.global.jwt.repository.RefreshTokenRepository;
import com.team11.shareoffice.global.util.ErrorCode;
import com.team11.shareoffice.member.dto.LoginRequestDto;
import com.team11.shareoffice.member.dto.SignupRequestDto;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
@Builder
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    public ResponseDto<?> signup(SignupRequestDto signupRequestDto){
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickname = signupRequestDto.getNickname();

        // 비밀번호와 확인 비밀번호 일치 확인
        if(!Objects.equals(signupRequestDto.getPassword(), signupRequestDto.getPasswordCheck())){
            throw new CustomException(ErrorCode.NOT_SAME_PASSWORD);
        }

        // 이메일 중복 검사
        Optional<Member> foundByEmail = memberRepository.findByEmail(email);
        if (foundByEmail.isPresent()){
            throw new CustomException(ErrorCode.EXIST_EMAIL);
        }

        // 닉네임 중복 검사
        Optional<Member> foundByUsername = memberRepository.findByNickname(nickname);
        if (foundByUsername.isPresent()){
            throw new CustomException(ErrorCode.EXIST_NICKNAME);
        }

        // 유저 등록
        Member member = Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname).build();

        memberRepository.save(member);

        return ResponseDto.setSuccess("회원가입에 성공했습니다.");

    }

    // 로그인
    public ResponseDto<?> login(LoginRequestDto loginRequestDto, HttpServletResponse response){
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        // 이메일 검사
        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member == null){
            throw new CustomException(ErrorCode.NOT_EXIST_EMAIL);
        }

        // 패스워드 검사
        if (!passwordEncoder.matches(password, member.getPassword())){
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

        //Token 생성
        TokenDto tokenDto = jwtUtil.createAllToken(member.getEmail());

        //RefreshToken 있는지 확인
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMember(member);

        //있으면 새 토큰 발급 후 업데이트
        //없으면 새로 만들고 DB에 저장
        if (refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
        } else {
            refreshTokenRepository.saveAndFlush(RefreshToken.saveToken(tokenDto.getRefreshToken(), member));
        }
        //header에 accesstoken, refreshtoken 추가
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());

        return ResponseDto.setSuccess("로그인 성공");
    }
}
