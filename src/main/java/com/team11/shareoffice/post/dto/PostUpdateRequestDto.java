package com.team11.shareoffice.post.dto;

import com.team11.shareoffice.post.entity.Amenities;
import com.team11.shareoffice.post.entity.OperatingTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class PostUpdateRequestDto{
    private String title;
    private String content;
    private String location;
    private int capacity;
    private int price;
    private OperatingTimeRequestDto operatingTime;
    private String contentDetails;
    private AmenitiesRequestDto amenities;
    private List<String> imageUrls;

    @Getter
    public class OperatingTimeRequestDto {
        private String openTime;
        private String closeTime;
        private String holidayTypes;
        private Map<String,Boolean> holidays = new HashMap<>();
    }

    @Getter
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
