package com.team11.shareoffice.like.dto;

import com.team11.shareoffice.like.entity.Likes;
import com.team11.shareoffice.post.entity.Post;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LikeResponseDto {
    private int likeCount;
    private boolean likeStatus;

    public LikeResponseDto(Post post, Likes likes) {
        this.likeCount = post.getLikeCount();
        this.likeStatus = likes.isLikeStatus();
    }
}