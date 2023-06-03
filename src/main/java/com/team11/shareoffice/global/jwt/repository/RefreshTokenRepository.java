//package com.team11.shareoffice.global.jwt.repository;
//
//import com.team11.shareoffice.global.jwt.entity.RefreshToken;
//import com.team11.shareoffice.member.entity.Member;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
//    Optional<RefreshToken> findByMember(Member member);
//
//    void deleteByMember(Member member);
//
//}
