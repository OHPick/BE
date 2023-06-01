package com.team11.shareoffice.post.dto;


import com.team11.shareoffice.post.entity.Post;
import lombok.Getter;
import lombok.Setter;

//image url, 좋아요개수
@Getter
@Setter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String location;
    private String imagUrl;
    private int likeCount;
    private int price;
    private int capacity;
    private boolean likeStatus;

    public PostResponseDto(Post post, boolean likeStatus) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.location = post.getLocation();
        this.imagUrl = post.getPostImage();
        this.likeCount = post.getLikeCount();
        this.price = post.getPrice();
        this.capacity = post.getCapacity();
        this.likeStatus = likeStatus;
    }
}
