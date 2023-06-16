package com.team11.shareoffice.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@Getter
@RequiredArgsConstructor
public class PostRequestDto {
    private String title;
    private String location;
    private int price;
    private int capacity;  //
    private String content;
    private String contentDetails; //
    private OperatingTimeRequestDto operatingTime;
    private AmenitiesRequestDto amenities;

    @Getter
    @RequiredArgsConstructor
    public class OperatingTimeRequestDto {
        private String openTime;
        private String closeTime;
        private String holidayTypes;
        private Map<String,Boolean> holidays = new HashMap<>();
    }

    @Getter
    @RequiredArgsConstructor
    public class AmenitiesRequestDto {
        private Boolean isAircon;
        private Boolean isCopierPrinter;
        private Boolean isProjector;
        private Boolean isDoorLock;
        private Boolean isPowerOutlet;
        private Boolean isFax;
        private Boolean isHeater;
        private Boolean isParking;
        private Boolean isWaterPurifier;
        private Boolean isPersonalLocker;
        private Boolean isTV;
        private Boolean isWhiteBoard;
        private Boolean isInternetWiFi;
    }

}


