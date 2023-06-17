package com.team11.shareoffice.member.dto;

import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.entity.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MyReservesResponseDto {
    private Long postId;
    private Long reservationId;
    private String title;
    private String content;
    private String location;
    private String imageUrl;
    private int likeCount;
    private int price;
    private int capacity;
    private String contentDetails;
    private boolean likeStatus;
    private int userStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isFinished;


    public MyReservesResponseDto(Post post, boolean likeStatus, int userStatus, Reservation reservation, boolean isFinished) {
        this.postId = post.getId();
        this.reservationId = reservation.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.location = post.getLocation();
        this.imageUrl = post.getPostImages().get(0);
        this.likeCount = post.getLikeCount();
        this.price = post.getPrice();
        this.capacity = post.getCapacity();
        this.contentDetails = post.getContentDetails();
        this.likeStatus = likeStatus;
        this.userStatus = userStatus;
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.isFinished = isFinished;
    }
}
