package com.team11.shareoffice.post.service;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.dto.PostRequestDto;
import com.team11.shareoffice.post.dto.PostUpdateRequestDto;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import com.team11.shareoffice.post.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostValidator postValidator;

    public ResponseDto<Long> createPost(PostRequestDto postRequestDto, Member member) {
        Post post = new Post(postRequestDto, member);
        postRepository.save(post);
        return ResponseDto.setSuccess(null);
    }

    public ResponseDto<Long> updatePost(Long id, PostUpdateRequestDto postRequestDto, Member member) {
        Post post = postValidator.validateIsExistPost(id);
        postValidator.validatePostAuthor(post, member);
        post.updatePost(postRequestDto);
        return ResponseDto.setSuccess(null);
    }

    public ResponseDto<Long> deletePost(Long id,Member member) {
        Post post = postValidator.validateIsExistPost(id);
        postValidator.validatePostAuthor(post, member);
        postRepository.delete(post);
        return ResponseDto.setSuccess(null);
    }
}
