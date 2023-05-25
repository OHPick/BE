package com.team11.shareoffice.like.repository;

import com.team11.shareoffice.like.entity.Likes;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    Likes findByMemberAndPost(Member member, Post post);

    void deleteLikesByPost(Post post);

}
