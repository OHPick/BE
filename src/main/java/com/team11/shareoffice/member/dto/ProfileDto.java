package com.team11.shareoffice.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDto {

    private String email;
    private String nickname;
    private String imageUrl;

    public ProfileDto(String email, String nickname, String imageUrl) {
        this.email = email;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }
}