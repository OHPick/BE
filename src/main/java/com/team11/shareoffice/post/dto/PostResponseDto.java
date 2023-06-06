package com.team11.shareoffice.post.dto;


import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.entity.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

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
    private LocalDate startDate;
    private LocalDate endDate;


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
    }

    public PostResponseDto(Post post, boolean likeStatus, int userStatus,Reservation reservation) {
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
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
    }
}
