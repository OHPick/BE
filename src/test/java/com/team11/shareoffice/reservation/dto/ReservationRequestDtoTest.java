package com.team11.shareoffice.reservation.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationRequestDtoTest {
    @Test
    void testReservationRequestDto_Normal() {
        ReservationRequestDto requestDto = new ReservationRequestDto();
        requestDto.setStartDate(LocalDate.of(2023,6,12));
        requestDto.setEndDate(LocalDate.of(2023,6,17));

        assertEquals("2023-06-12", String.valueOf(requestDto.getStartDate()));
        assertEquals("2023-06-17", String.valueOf(requestDto.getEndDate()));
    }
}