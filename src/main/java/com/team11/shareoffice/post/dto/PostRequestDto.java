package com.team11.shareoffice.post.dto;

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
    public class OperatingTimeRequestDto {
        private String openTime;
        private String closeTime;
        private String holidayTypes;
        private Map<String,Boolean> holidays = new HashMap<>();
    }

    @Getter
    public class AmenitiesRequestDto {
        private boolean isAircon;
        private boolean isCopierPrinter;
        private boolean isProjector;
        private boolean isDoorLock;
        private boolean isPowerOutlet;
        private boolean isFax;
        private boolean isHeater;
        private boolean isParking;
        private boolean isWaterPurifier;
        private boolean isPersonalLocker;
        private boolean isTV;
        private boolean isWhiteBoard;
        private boolean isInternetWiFi;
    }

}


