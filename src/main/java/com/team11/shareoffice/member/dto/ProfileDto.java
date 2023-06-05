package com.team11.shareoffice.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDto {


    private String nickname;


    public ProfileDto(String nickname) {

        this.nickname = nickname;

    }
}