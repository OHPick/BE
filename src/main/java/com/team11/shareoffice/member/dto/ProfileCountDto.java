package com.team11.shareoffice.member.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileCountDto {

    private String email;
    private String nickname;
    private String imageUrl;
    private int postCount;
    private int likeCount;
    private int reserveCount;

    public ProfileCountDto(String email, String nickname, String imageUrl, int postCount, int likeCount, int reserveCount) {
        this.email = email;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.postCount = postCount;
        this.likeCount = likeCount;
        this.reserveCount =reserveCount;
    }
}