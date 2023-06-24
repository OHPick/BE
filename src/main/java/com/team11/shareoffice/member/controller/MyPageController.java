package com.team11.shareoffice.member.controller;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.member.dto.MyReservesResponseDto;
import com.team11.shareoffice.member.dto.ProfileCountDto;
import com.team11.shareoffice.member.dto.ProfileDto;
import com.team11.shareoffice.member.service.MyPageService;
import com.team11.shareoffice.post.dto.PostResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;


    //나의 게시글들 조회하기
    @Operation(summary = "내가 작성한 게시글 API", description = "나의 작성 게시글")
    @GetMapping("/myposts")
    public ResponseDto<List<PostResponseDto>> getMyPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseDto.setSuccess("내 게시글목록 조회성공", myPageService.getMyPosts(userDetails.getMember()));
    }

    //좋아요한 게시글들 조회하기
    @Operation(summary = "내가 좋아요한 게시글 API", description = "나의 좋아요 게시글")
    @GetMapping("/mylikes")
    public ResponseDto<List<PostResponseDto>> getMyLikes(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseDto.setSuccess("내 좋아요 목록 조회 성공", myPageService.getMyLikes(userDetails.getMember()));
    }

    //예약 현황 조회하기
    @Operation(summary = "나의 예약 현황 API", description = "예약현황")
    @GetMapping("/myreserves")
    public ResponseDto<List<MyReservesResponseDto>> getMyReserves(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseDto.setSuccess("나의 예약 현황 목록 조회 완료", myPageService.getMyReserves(userDetails.getMember()));
    }

    // MyPage Profile 조회.
    @Operation(summary = "프로필 API", description = "프로필")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "프로필 조회 완료")})
    @GetMapping()
    public ResponseDto<ProfileCountDto> profile(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseDto.setSuccess("프로필 조회성공",myPageService.profile(userDetails.getMember()));
    }

    // MyPage Profile 수정
    @Operation(summary = "프로필 수정 API", description = "프로필 수정")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "프로필 수정 완료")})
    @PutMapping("/modify")
    public ResponseDto<ProfileDto> profileModify(@RequestPart(value = "profileDto", required = false) ProfileDto profileDto,
                                                 @RequestPart(value = "imageFile", required = false) MultipartFile image,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return ResponseDto.setSuccess("프로필 수정 성공", myPageService.profileModify(profileDto, image, userDetails.getMember()));
    }

}
