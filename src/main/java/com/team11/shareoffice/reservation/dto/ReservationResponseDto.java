package com.team11.shareoffice.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.entity.Reservation;
import lombok.Setter;

import java.time.LocalDate;

@Setter
public class ReservationResponseDto {
    @JsonFormat
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;
    @JsonFormat
    private String location;

    public ReservationResponseDto(Post post, Reservation reservation) {
        this.title = post.getTitle();
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.location = post.getLocation();
    }
}
