package com.team11.shareoffice.member.service;

import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.exception.ErrorMessage;
import com.team11.shareoffice.global.jwt.JwtUtil;
import com.team11.shareoffice.member.dto.LoginRequestDto;
import com.team11.shareoffice.member.dto.MessageDto;
import com.team11.shareoffice.member.dto.SignupRequestDto;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ResponseEntity<MessageDto> signup(SignupRequestDto signupRequestDto){
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String username = signupRequestDto.getUsername();

        // 이메일 중복 검사
        Optional<Member> foundByEmail = memberRepository.findByEmail(email);
        if (foundByEmail.isPresent()){
            throw new CustomException(ErrorMessage.ENROLLED_EMAIL);
        }

        // 닉네임 중복 검사
        Optional<Member> foundByUsername = memberRepository.findByUsername(username);
        if (foundByUsername.isPresent()){
            throw new CustomException(ErrorMessage.ENROLLED_NICKNAME);
        }

        // 유저 등록
        Member member = Member.builder()
                .email(email)
                .password(password)
                .username(username).build();

        memberRepository.save(member);

        return ResponseEntity.ok().body(new MessageDto("회원가입에 성공했습니다."));
    }

    // 로그인
    public ResponseEntity<MessageDto> login(LoginRequestDto loginRequestDto, HttpServletResponse response){
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        // 이메일 검사
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorMessage.UNENROLLED_EMAIL));

        // 패스워드 검사
        if (!passwordEncoder.matches(password, member.getPassword())){
            throw new CustomException(ErrorMessage.PASSWORD_MISMATCH);
        }

        // 토큰 발급
        String token = jwtUtil.createToken(member);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        return ResponseEntity.ok().body(new MessageDto("로그인 성공"));
    }
}
