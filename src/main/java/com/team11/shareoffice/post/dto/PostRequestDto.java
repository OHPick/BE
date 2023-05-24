package com.team11.shareoffice.post.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class PostRequestDto {
    private String title;
    private String content;
    private String location;
    private int price;

}
