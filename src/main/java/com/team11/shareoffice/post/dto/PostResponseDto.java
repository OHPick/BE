package com.team11.shareoffice.post.dto;


import com.team11.shareoffice.post.entity.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String location;
    private String imageUrl;
    private int likeCount;
    private int price;
    private int capacity;
    private boolean likeStatus;
    private int userStatus;

    public PostResponseDto(Post post, boolean likeStatus, int userStatus) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.location = post.getLocation();
        this.imageUrl = post.getPostImage();
        this.likeCount = post.getLikeCount();
        this.price = post.getPrice();
        this.capacity = post.getCapacity();
        this.likeStatus = likeStatus;
        this.userStatus = userStatus;
    }
}
