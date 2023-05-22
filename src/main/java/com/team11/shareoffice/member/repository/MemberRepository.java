package com.team11.shareoffice.member.repository;

import com.team11.shareoffice.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
