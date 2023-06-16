package com.team11.shareoffice.post.entity;

import com.team11.shareoffice.post.dto.AmenitiesRequestDto;
import com.team11.shareoffice.post.dto.PostRequestDto;
import com.team11.shareoffice.post.dto.PostUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Amenities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column
    private boolean isAircon;
    @Column
    private boolean isCopierPrinter;
    @Column
    private boolean isProjector;
    @Column
    private boolean isDoorLock;
    @Column
    private boolean isPowerOutlet;
    @Column
    private boolean isFax;
    @Column
    private boolean isHeater;
    @Column
    private boolean isParking;
    @Column
    private boolean isWaterPurifier;
    @Column
    private boolean isPersonalLocker;
    @Column
    private boolean isTV;
    @Column
    private boolean isWhiteBoard;
    @Column
    private boolean isInternetWiFi;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public Amenities (PostRequestDto.AmenitiesRequestDto amenitiesRequestDto){
        this.isAircon = amenitiesRequestDto.isAircon();
        this.isCopierPrinter = amenitiesRequestDto.isCopierPrinter();
        this.isProjector = amenitiesRequestDto.isProjector();
        this.isDoorLock = amenitiesRequestDto.isDoorLock();
        this.isPowerOutlet = amenitiesRequestDto.isPowerOutlet();
        this.isFax = amenitiesRequestDto.isFax();
        this.isHeater = amenitiesRequestDto.isHeater();
        this.isParking = amenitiesRequestDto.isParking();
        this.isWaterPurifier = amenitiesRequestDto.isWaterPurifier();
        this.isPersonalLocker = amenitiesRequestDto.isPersonalLocker();
        this.isTV = amenitiesRequestDto.isTV();
        this.isWhiteBoard = amenitiesRequestDto.isWhiteBoard();
        this.isInternetWiFi = amenitiesRequestDto.isInternetWiFi();
    }

    public void updateAmenities (PostUpdateRequestDto.AmenitiesRequestDto amenitiesRequestDto){
        this.isAircon = amenitiesRequestDto.isAircon();
        this.isCopierPrinter = amenitiesRequestDto.isCopierPrinter();
        this.isProjector = amenitiesRequestDto.isProjector();
        this.isDoorLock = amenitiesRequestDto.isDoorLock();
        this.isPowerOutlet = amenitiesRequestDto.isPowerOutlet();
        this.isFax = amenitiesRequestDto.isFax();
        this.isHeater = amenitiesRequestDto.isHeater();
        this.isParking = amenitiesRequestDto.isParking();
        this.isWaterPurifier = amenitiesRequestDto.isWaterPurifier();
        this.isPersonalLocker = amenitiesRequestDto.isPersonalLocker();
        this.isTV = amenitiesRequestDto.isTV();
        this.isWhiteBoard = amenitiesRequestDto.isWhiteBoard();
        this.isInternetWiFi = amenitiesRequestDto.isInternetWiFi();
    }

}
