package com.team11.shareoffice.like.repository;

import com.team11.shareoffice.like.entity.Likes;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    Likes findByMemberAndPost(Member member, Post post);
    List<Likes> findAllByMemberAndLikeStatus(Member member, boolean likeStatus);

    Likes findByMemberIdAndPostId(Long memberId, Long postId);

    List<Likes> findAllByPost(Post post);

    void deleteLikesByPost(Post post);

}
