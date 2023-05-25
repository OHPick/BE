package com.team11.shareoffice.post.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostUpdateRequestDto{
    private String title;
    private String content;
    private String location;
}
