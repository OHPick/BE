package com.team11.shareoffice.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity(name = "members")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column
    private Long kakaoId;

    @ColumnDefault("https://shareoffice12.s3.ap-northeast-2.amazonaws.com/image.png")
    private String imageUrl;

    public Member(String email, Long kakaoId, String password, String nickname){
        this.email = email;
        this.kakaoId = kakaoId;
        this.password = password;
        this.nickname = nickname;
    }

    public Member(String email, String password, String nickname){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public Member kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }
}
