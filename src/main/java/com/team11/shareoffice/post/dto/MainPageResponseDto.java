package com.team11.shareoffice.post.dto;

import com.team11.shareoffice.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MainPageResponseDto {
    private Long id;
    private String title;
    private String content;
    private String location;
    private int price;
    private int likeCount;
    private String postImage;
    private String memberNickname;

    public MainPageResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.location = post.getLocation();
        this.price = post.getPrice();
        this.likeCount = post.getLikeCount();
        this.postImage = post.getPostImage();
        this.memberNickname = post.getMember().getNickname();
    }
}
