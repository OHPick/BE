package com.team11.shareoffice.member.controller;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.member.dto.LoginRequestDto;
import com.team11.shareoffice.member.dto.ProfileDto;
import com.team11.shareoffice.member.dto.SignupRequestDto;
import com.team11.shareoffice.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // Sign up
    @Operation(summary = "회원가입 API", description = "회원가입")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "회원 가입 완료")})
    @PostMapping("/signup")
    public ResponseDto<?> signup(@RequestBody @Valid SignupRequestDto signupRequestDto){
        return memberService.signup(signupRequestDto);
    }

    // Login
    @Operation(summary = "로그인 API", description = "로그인 성공시 jwt 토큰을 헤더에 넣어 반환합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "로그인 완료")})
    @PostMapping("/login")
    public ResponseDto<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        return memberService.login(loginRequestDto, response);
    }

    // Profile
    @Operation(summary = "프로필 API", description = "프로필")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "프로필 조회 완료")})
    @GetMapping("/myprofile")
    public ResponseDto<ProfileDto> profile(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return memberService.profile(userDetails.getMember());
    }

    // Profile 수정
    @Operation(summary = "프로필 수정 API", description = "프로필 수정")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "프로필 수정 완료")})
    @PutMapping("/myprofile/modify")
    public ResponseDto<ProfileDto> profileModify(@RequestPart(value = "profileDto", required = false) ProfileDto profileDto,
                                         @RequestPart(value = "imageFile", required = false) MultipartFile image,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return memberService.profileModify(profileDto, image, userDetails.getMember());
    }
}
