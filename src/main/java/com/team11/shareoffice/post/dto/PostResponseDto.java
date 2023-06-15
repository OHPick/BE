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
    private String operatingTime;
    private String contentDetails;
    private String amenities;
    private boolean likeStatus;
    private int userStatus;
    private String nickname;


    public PostResponseDto(Post post, boolean likeStatus, int userStatus) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.location = post.getLocation();
        this.imageUrl = post.getPostImage();
        this.likeCount = post.getLikeCount();
        this.price = post.getPrice();
        this.capacity = post.getCapacity();
        this.operatingTime = post.getOperatingTime();
        this.contentDetails = post.getContentDetails();
        this.amenities = post.getAmenities();
        this.likeStatus = likeStatus;
        this.userStatus = userStatus;
        this.nickname = post.getMember().getNickname();
    }

}
