package com.team11.shareoffice.member.dto;

import lombok.Getter;

@Getter
public class UserInfoDto {
    private Long kakaoId;
    private String nickname;
    private String email;


    public UserInfoDto(Long kakaoId, String nickname, String email){
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.email = email;
    }
}