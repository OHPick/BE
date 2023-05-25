package com.team11.shareoffice.like.service;


import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.like.LikeValidator.LikeValidator;
import com.team11.shareoffice.like.dto.LikeResponseDto;
import com.team11.shareoffice.like.entity.Likes;
import com.team11.shareoffice.like.repository.LikeRepository;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final LikeValidator likeValidator;

    @Transactional
    public ResponseDto<?> likePost(Long postId, Member member) {

        //게시글 확인
        Post post = likeValidator.validateIsExistPost(postId);

        // 로그인 여부 확인
        likeValidator.validateIsLogin(member);

        //좋아요 존재여부 확인
        Likes like = likeRepository.findByMemberAndPost(member, post);
        if (like == null) {
            Likes newLikes = likeRepository.save(Likes.addLike(member, post));
            newLikes.setLikeStatus();
            post.updateLike(true);
            return ResponseDto.setSuccess(new LikeResponseDto(post, newLikes));
        } else {
            if (!like.isLikeStatus()) {
                like.setLikeStatus();
                post.updateLike(true);
            } else {
                like.setLikeStatus();
                post.updateLike(false);
            }
            return ResponseDto.setSuccess(new LikeResponseDto(post, like));
        }
    }
}