package com.team11.shareoffice.post.dto;

import com.team11.shareoffice.post.entity.Post;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
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

