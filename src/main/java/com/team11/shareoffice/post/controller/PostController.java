package com.team11.shareoffice.post.controller;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.post.dto.MainPageResponseDto;
import com.team11.shareoffice.post.dto.PostRequestDto;
import com.team11.shareoffice.post.dto.PostResponseDto;
import com.team11.shareoffice.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Operation(summary = "메인페이지 API", description = "메인페이지 반환")
    @GetMapping
    public Page<MainPageResponseDto> getPosts(@AuthenticationPrincipal @Nullable UserDetailsImpl userDetails,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) String district,
                                              @RequestParam(required = false) String sorting,
                                              Pageable pageable) {
        return postService.findPosts(userDetails, keyword, district, sorting, pageable);
    }

    @Operation(summary = "게시글작성 API", description = "게시글작성")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseDto<?> createPost(@RequestPart PostRequestDto postRequestDto,
                                        @RequestPart(value = "imageFile", required = false) List<MultipartFile> image,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        return ResponseDto.setSuccess("게시글 작성 성공", postService.createPost(postRequestDto,  image, userDetails.getMember()));

    }

    @Operation(summary = "게시글수정 API", description = "게시글수정")
    @PutMapping("/{id}")
    public ResponseDto<?> updatePost(@PathVariable Long id,
                                        @RequestPart(value = "postRequestDto", required = false) PostRequestDto postRequestDto,
                                        @RequestPart(value = "imageFile", required = false) List<MultipartFile> image,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException{

        postService.updatePost(id, postRequestDto,  image, userDetails.getMember());
        return ResponseDto.setSuccess("게시글 수정 성공");
    }

    @Operation(summary = "게시글삭제 API", description = "게시글삭제")
    @DeleteMapping("/{id}")
    public ResponseDto<?> deletePost(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(id, userDetails.getMember());
        return ResponseDto.setSuccess("게시글 삭제 성공");
    }


    //상세 게시글 조회
    @Operation(summary = "상세게시글 API", description = "상세게시글")
    @GetMapping("/{id}")
    public ResponseDto<PostResponseDto> getPost(@PathVariable Long id) {
        if (isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ResponseDto.setSuccess("상세 게시글 조회 성공",  postService.getPost(id, userDetails.getMember()));
        }
        return ResponseDto.setSuccess("상세 게시글 조회 성공", postService.getPost(id,null));
    }

    //로그인 여부 확인
    @Operation(summary = "로그인 여부 확인 API")
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
}
