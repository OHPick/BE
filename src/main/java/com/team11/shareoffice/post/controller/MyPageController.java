package com.team11.shareoffice.post.controller;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.post.dto.PostResponseDto;
import com.team11.shareoffice.post.service.MyPageService;
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
    @GetMapping("/myposts")
    public ResponseDto<List<PostResponseDto>> getMyPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPosts(userDetails.getMember());
    }

    //좋아요한 게시글들 조회하기
    @GetMapping("/myposts/likes")
    public ResponseDto<List<PostResponseDto>> getMyLikes(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyLikes(userDetails.getMember());
    }

    //예약 현황 조회하기
    @GetMapping("/myposts/reserves")
    public ResponseDto<List<PostResponseDto>> getMyReserves(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyReserves(userDetails.getMember());
    }
}
