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
    private Long id;
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
        this.id = post.getId();
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
        this.id = post.getId();
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
        public boolean isAircon;
        public boolean isCopierPrinter;
        public boolean isProjector;
        public boolean isDoorLock;
        public boolean isPowerOutlet;
        public boolean isFax;
        public boolean isHeater;
        public boolean isParking;
        public boolean isWaterPurifier;
        public boolean isPersonalLocker;
        public boolean isTV;
        public boolean isWhiteBoard;
        public boolean isInternetWiFi;

        public AmenitiesResponseDto(Amenities amenities) {
            this.isAircon = amenities.isAircon();
            this.isCopierPrinter = amenities.isCopierPrinter();
            this.isProjector = amenities.isProjector();
            this.isDoorLock = amenities.isDoorLock();
            this.isPowerOutlet = amenities.isPowerOutlet();
            this.isFax = amenities.isFax();
            this.isHeater = amenities.isHeater();
            this.isParking = amenities.isParking();
            this.isWaterPurifier = amenities.isWaterPurifier();
            this.isPersonalLocker = amenities.isPersonalLocker();
            this.isTV = amenities.isTV();
            this.isWhiteBoard = amenities.isWhiteBoard();
            this.isInternetWiFi = amenities.isInternetWiFi();
        }
    }
}
