package com.team11.shareoffice.member.controller;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.member.dto.LoginRequestDto;
import com.team11.shareoffice.member.dto.SignoutRequestDto;
import com.team11.shareoffice.member.dto.SignupRequestDto;
import com.team11.shareoffice.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // Sign up
    @Operation(summary = "회원가입 API", description = "회원가입")
    @PostMapping("/signup")
    public ResponseDto<?> signup(@RequestBody SignupRequestDto signupRequestDto){
        memberService.signup(signupRequestDto);
        return ResponseDto.setSuccess("회원가입에 성공했습니다.");
    }

    // Login
    @Operation(summary = "로그인 API", description = "로그인 성공시 jwt 토큰을 헤더에 넣어 반환합니다.")
    @PostMapping("/login")
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response){
        memberService.login(requestDto, response);
        return ResponseDto.setSuccess("로그인 성공");
    }

    @Operation(summary = "로그아웃API")
    @PostMapping("/logout")
    public ResponseDto<?> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        memberService.logout(userDetails.getMember(), request);
        return ResponseDto.setSuccess("로그아웃 성공");
    }

    @Operation(summary = "회원탈퇴API")
    @DeleteMapping("/signout")
    public ResponseDto<?> signout(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody SignoutRequestDto request){
        memberService.signout(userDetails, request);
        return ResponseDto.setSuccess("회원탈퇴 성공");
    }

}
