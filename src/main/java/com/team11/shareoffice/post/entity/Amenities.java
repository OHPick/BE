package com.team11.shareoffice.post.entity;

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
    private Boolean isAircon;
    @Column
    private Boolean isCopierPrinter;
    @Column
    private Boolean isProjector;
    @Column
    private Boolean isDoorLock;
    @Column
    private Boolean isPowerOutlet;
    @Column
    private Boolean isFax;
    @Column
    private Boolean isHeater;
    @Column
    private Boolean isParking;
    @Column
    private Boolean isWaterPurifier;
    @Column
    private Boolean isPersonalLocker;
    @Column
    private Boolean isTV;
    @Column
    private Boolean isWhiteBoard;
    @Column
    private Boolean isInternetWiFi;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public Amenities (PostRequestDto.AmenitiesRequestDto amenitiesRequestDto){
        this.isAircon = amenitiesRequestDto.getIsAircon();
        this.isCopierPrinter = amenitiesRequestDto.getIsCopierPrinter();
        this.isProjector = amenitiesRequestDto.getIsProjector();
        this.isDoorLock = amenitiesRequestDto.getIsDoorLock();
        this.isPowerOutlet = amenitiesRequestDto.getIsPowerOutlet();
        this.isFax = amenitiesRequestDto.getIsFax();
        this.isHeater = amenitiesRequestDto.getIsHeater();
        this.isParking = amenitiesRequestDto.getIsParking();
        this.isWaterPurifier = amenitiesRequestDto.getIsWaterPurifier();
        this.isPersonalLocker = amenitiesRequestDto.getIsPersonalLocker();
        this.isTV = amenitiesRequestDto.getIsTV();
        this.isWhiteBoard = amenitiesRequestDto.getIsWhiteBoard();
        this.isInternetWiFi = amenitiesRequestDto.getIsInternetWiFi();
    }

    public void updateAmenities (PostUpdateRequestDto.AmenitiesRequestDto amenitiesRequestDto){
        this.isAircon = amenitiesRequestDto.getIsAircon();
        this.isCopierPrinter = amenitiesRequestDto.getIsCopierPrinter();
        this.isProjector = amenitiesRequestDto.getIsProjector();
        this.isDoorLock = amenitiesRequestDto.getIsDoorLock();
        this.isPowerOutlet = amenitiesRequestDto.getIsPowerOutlet();
        this.isFax = amenitiesRequestDto.getIsFax();
        this.isHeater = amenitiesRequestDto.getIsHeater();
        this.isParking = amenitiesRequestDto.getIsParking();
        this.isWaterPurifier = amenitiesRequestDto.getIsWaterPurifier();
        this.isPersonalLocker = amenitiesRequestDto.getIsPersonalLocker();
        this.isTV = amenitiesRequestDto.getIsTV();
        this.isWhiteBoard = amenitiesRequestDto.getIsWhiteBoard();
        this.isInternetWiFi = amenitiesRequestDto.getIsInternetWiFi();
    }

}
