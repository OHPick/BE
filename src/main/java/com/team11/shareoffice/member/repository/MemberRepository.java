package com.team11.shareoffice.member.repository;

import com.team11.shareoffice.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByKakaoId(Long kakaoId);

    Optional<Member> findByNickname(String nickname);
}
