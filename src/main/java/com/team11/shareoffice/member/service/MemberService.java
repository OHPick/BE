package com.team11.shareoffice.member.service;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.jwt.JwtUtil;
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

    // 회원가입
    public ResponseDto<?> signup(SignupRequestDto signupRequestDto){
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickname = signupRequestDto.getNickname();

        // 비밀번호와 확인 비밀번호 일치 확인
        if(!Objects.equals(signupRequestDto.getPassword(), signupRequestDto.getPasswordCheck())){
            return ResponseDto.setBadRequest("비밀번호가 서로 일치하지 않습니다.");
        }

        // 이메일 중복 검사
        Optional<Member> foundByEmail = memberRepository.findByEmail(email);
        if (foundByEmail.isPresent()){
            return ResponseDto.setBadRequest("이미 등록된 이메일 입니다.");
        }

        // 닉네임 중복 검사
        Optional<Member> foundByUsername = memberRepository.findByNickname(nickname);
        if (foundByUsername.isPresent()){
            return ResponseDto.setBadRequest("이미 등록된 닉네임 입니다.");
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
            return ResponseDto.setBadRequest("등록되지 않은 이메일입니다.");
        }

        // 패스워드 검사
        if (!passwordEncoder.matches(password, member.getPassword())){
            return ResponseDto.setBadRequest("비밀번호를 확인해주세요.");
        }

        // 토큰 발급
        String token = jwtUtil.createToken(member);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        return ResponseDto.setSuccess("로그인 성공");
    }
}
