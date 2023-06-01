package com.team11.shareoffice.post.controller;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.post.dto.PostResponseDto;
import com.team11.shareoffice.post.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MyPageController {

    private final MyPageService myPageService;


    //나의 게시글들 조회하기
    @Operation(summary = "내가 작성한 게시글 API", description = "나의 작성 게시글")
    @GetMapping("/myposts")
    public ResponseDto<List<PostResponseDto>> getMyPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPosts(userDetails.getMember());
    }

    //좋아요한 게시글들 조회하기
    @Operation(summary = "내가 좋아요한 게시글 API", description = "나의 좋아요 게시글")
    @GetMapping("/myposts/likes")
    public ResponseDto<List<PostResponseDto>> getMyLikes(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyLikes(userDetails.getMember());
    }

    //예약 현황 조회하기
    @Operation(summary = "나의 예약 현황 API", description = "예약현황")
    @GetMapping("/myposts/reserves")
    public ResponseDto<List<PostResponseDto>> getMyReserves(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyReserves(userDetails.getMember());
    }
}
