package com.team11.shareoffice.member.dto;

import lombok.Getter;

@Getter
public class UserInfoDto {
    private Long kakaoId;
    private String nickname;
    private String email;
    private String imageUrl;


    public UserInfoDto(Long kakaoId, String nickname, String email, String imageUrl){
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.email = email;
        this.imageUrl = imageUrl;
    }
}