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
