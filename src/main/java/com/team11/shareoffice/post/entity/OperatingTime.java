package com.team11.shareoffice.post.entity;

import com.team11.shareoffice.post.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperatingTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String openTime;

    @Column
    private String closeTime;

    @Column
    private String holidayTypes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ElementCollection
    @CollectionTable(name = "holidays_mapping", joinColumns = @JoinColumn(name = "operating_time_id"))
    @MapKeyColumn(name = "weekday")
    @Column(name = "is_holiday")
    private Map<String,Boolean> holidays = new HashMap<>();

    public OperatingTime(PostRequestDto.OperatingTimeRequestDto operatingTimeRequestDto) {
        this.openTime = operatingTimeRequestDto.getOpenTime();
        this.closeTime = operatingTimeRequestDto.getCloseTime();
        this.holidayTypes = operatingTimeRequestDto.getHolidayTypes();
        this.holidays = operatingTimeRequestDto.getHolidays();
    }

    public void updateOperatingTime(PostRequestDto.OperatingTimeRequestDto operatingTimeRequestDto) {
        this.openTime = operatingTimeRequestDto.getOpenTime();
        this.closeTime = operatingTimeRequestDto.getCloseTime();
        this.holidayTypes = operatingTimeRequestDto.getHolidayTypes();
        this.holidays = operatingTimeRequestDto.getHolidays();
    }
}
