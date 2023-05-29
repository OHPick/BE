package com.team11.shareoffice.global.jwt.entity;

import com.team11.shareoffice.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refreshToken_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private RefreshToken(String refreshToken, Member member) {
        this.refreshToken = refreshToken;
        this.member = member;
    }

    public static RefreshToken saveToken(String refreshToken, Member member) {
        return new RefreshToken(refreshToken, member);
    }

    public RefreshToken updateToken(String token) {
        this.refreshToken = token;
        return this;
    }
}