package com.team11.shareoffice.post.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class PostUpdateRequestDto{
    private String title;
    private String content;
    private String location;
    private int capacity;
    private int price;
    private String operatingTime;
    private String contentDetails;
    private String amenities;
    private List<String> imageUrls;
}
