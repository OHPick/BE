package com.team11.shareoffice.post.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class PostRequestDto {
    private String title;
    private String location;
    private int price;
    private int capacity;  //
    private String content;
    private String operatingTime; //
    private String contentDetails; //
    private String amenities; //

}
