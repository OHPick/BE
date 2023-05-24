package com.team11.shareoffice.post.controller;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.post.dto.PostRequestDto;
import com.team11.shareoffice.post.dto.PostUpdateRequestDto;
import com.team11.shareoffice.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseDto<Long> createPost(@RequestPart PostRequestDto postRequestDto,
                                        @RequestPart(value = "imageFile", required = false) MultipartFile image,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return postService.createPost(postRequestDto, image, userDetails.getMember());
    }

    @PutMapping("/{id}")
    public ResponseDto<Long> updatePost(@PathVariable Long id, @RequestBody PostUpdateRequestDto postRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePost(id, postRequestDto, userDetails.getMember());
    }

    @DeleteMapping("/{id}")
    public ResponseDto<Long> deletePost(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deletePost(id, userDetails.getMember());
    }
}
