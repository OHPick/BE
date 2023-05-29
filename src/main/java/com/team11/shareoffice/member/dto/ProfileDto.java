package com.team11.shareoffice.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDto {

    private String email;
    private String nickName;
    private String imageUrl;

    public ProfileDto(String email, String nickName, String imageUrl) {
        this.email = email;
        this.nickName = nickName;
        this.imageUrl = imageUrl;
    }
}