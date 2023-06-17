package com.team11.shareoffice.post.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.team11.shareoffice.post.entity.Amenities;
import com.team11.shareoffice.post.entity.OperatingTime;
import com.team11.shareoffice.post.entity.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String location;
    private List<String> imageUrl;
    private int likeCount;
    private int price;
    private int capacity;
    private String contentDetails;
    private boolean likeStatus;
    private int userStatus;
    private String nickname;
    @JsonFormat
    private AmenitiesResponseDto amenities;
    @JsonFormat
    private OperatingTimeResponseDto operatingTime;


    public PostResponseDto(Post post, boolean likeStatus, int userStatus, OperatingTime operatingTime, Amenities amenities) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.location = post.getLocation();
        this.imageUrl = post.getPostImages();
        this.likeCount = post.getLikeCount();
        this.price = post.getPrice();
        this.capacity = post.getCapacity();
        this.contentDetails = post.getContentDetails();
        this.likeStatus = likeStatus;
        this.userStatus = userStatus;
        this.nickname = post.getMember().getNickname();
        this.operatingTime = new OperatingTimeResponseDto(operatingTime);
        this.amenities = new AmenitiesResponseDto(amenities);
    }

    public PostResponseDto(Post post, boolean likeStatus, int userStatus) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.location = post.getLocation();
        this.imageUrl = post.getPostImages();
        this.likeCount = post.getLikeCount();
        this.price = post.getPrice();
        this.capacity = post.getCapacity();
        this.contentDetails = post.getContentDetails();
        this.likeStatus = likeStatus;
        this.userStatus = userStatus;
        this.nickname = post.getMember().getNickname();
    }

    public class OperatingTimeResponseDto {
        public String openTime;
        public String closeTime;
        public String holidayTypes;
        public Map<String,Boolean> holidays = new HashMap<>();

        public OperatingTimeResponseDto(OperatingTime operatingTime){
            this.openTime = operatingTime.getOpenTime();
            this.closeTime = operatingTime.getCloseTime();
            this.holidayTypes = operatingTime.getHolidayTypes();
            this.holidays = operatingTime.getHolidays();
        }
    }

    public class AmenitiesResponseDto {
        public Boolean isAircon;
        public Boolean isCopierPrinter;
        public Boolean isProjector;
        public Boolean isDoorLock;
        public Boolean isPowerOutlet;
        public Boolean isFax;
        public Boolean isHeater;
        public Boolean isParking;
        public Boolean isWaterPurifier;
        public Boolean isPersonalLocker;
        public Boolean isTV;
        public Boolean isWhiteBoard;
        public Boolean isInternetWiFi;

        public AmenitiesResponseDto(Amenities amenities) {
            this.isAircon = amenities.getIsAircon();
            this.isCopierPrinter = amenities.getIsCopierPrinter();
            this.isProjector = amenities.getIsProjector();
            this.isDoorLock = amenities.getIsDoorLock();
            this.isPowerOutlet = amenities.getIsPowerOutlet();
            this.isFax = amenities.getIsFax();
            this.isHeater = amenities.getIsHeater();
            this.isParking = amenities.getIsParking();
            this.isWaterPurifier = amenities.getIsWaterPurifier();
            this.isPersonalLocker = amenities.getIsPersonalLocker();
            this.isTV = amenities.getIsTV();
            this.isWhiteBoard = amenities.getIsWhiteBoard();
            this.isInternetWiFi = amenities.getIsInternetWiFi();
        }
    }
}
