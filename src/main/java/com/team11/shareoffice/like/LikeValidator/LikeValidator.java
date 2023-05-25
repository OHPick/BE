package com.team11.shareoffice.like.LikeValidator;

import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final PostRepository postRepository;

    //프로젝트 존재 여부 확인
    public Post validateIsExistPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
    }

    // 로그인 여부 확인
    public void validateIsLogin(Member member) {
        if (member == null) {
            throw new IllegalArgumentException();
        }
    }
}
