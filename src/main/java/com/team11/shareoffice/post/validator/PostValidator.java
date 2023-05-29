package com.team11.shareoffice.post.validator;

import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.util.ErrorCode;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final PostRepository postRepository;

    public Post validateIsExistPost(Long id){
        return postRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST));
    }

    public void validatePostAuthor(Post post, Member member){
        if(!post.getMember().getEmail().equals(member.getEmail())){
            throw new IllegalArgumentException("게시글의 작성자만 수정 / 삭제가 가능합니다.");
        }
    }
}
