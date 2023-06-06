package com.team11.shareoffice.member.controller;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.member.dto.LoginRequestDto;
import com.team11.shareoffice.member.dto.SignoutRequestDto;
import com.team11.shareoffice.member.dto.SignupRequestDto;
import com.team11.shareoffice.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // Sign up
    @Operation(summary = "회원가입 API", description = "회원가입")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "회원 가입 완료")})
    @PostMapping("/signup")
    public ResponseDto<?> signup(@RequestBody SignupRequestDto signupRequestDto){
        return memberService.signup(signupRequestDto);
    }

    // Login
    @Operation(summary = "로그인 API", description = "로그인 성공시 jwt 토큰을 헤더에 넣어 반환합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "로그인 완료")})
    @PostMapping("/login")
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response){
        return memberService.login(requestDto, response);
    }

    @Operation(summary = "로그아웃API")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "로그아웃 완료")})
    @PostMapping("/logout")
    public ResponseDto<String> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        memberService.logout(userDetails.getMember(), request);
        return ResponseDto.setSuccess("로그아웃 성공");
    }

    @Operation(summary = "회원탈퇴API")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "회원탈퇴 완료")})
    @DeleteMapping("/signout")
    public ResponseDto<?> signout(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody SignoutRequestDto request){
        return memberService.signout(userDetails, request);
    }

}
