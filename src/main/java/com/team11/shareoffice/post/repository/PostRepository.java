package com.team11.shareoffice.post.repository;


import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    List<Post> findAllByMemberOrderByCreatedAt(Member member);

    List<Post> findAllByMemberId(Long memberId);

    List<Post> findAllByMemberAndIsDeleteFalseOrderByCreatedAt(Member member);
}
