package com.team11.shareoffice.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.dto.ReservationRequestDto;
import com.team11.shareoffice.reservation.dto.ReservationResponseDto;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class ReservationControllerTest {

    @InjectMocks
    private ReservationController reservationController;
    @Mock
    private ReservationService reservationService;
    private MockMvc mockMvc;
    @BeforeEach
    void setMockMvc(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
    }

    @Test
    @DisplayName("예약하기")
    void reservePostSuccessTest() throws Exception {
        Long id = 1L;
        ReservationRequestDto requestDto = new ReservationRequestDto();
        requestDto.setStartDate(LocalDate.of(2023,6,12));
        requestDto.setEndDate(LocalDate.of(2023,6,17));

        Member member = new Member();
        doNothing().when(reservationService).reservePost(id, requestDto, member);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonRequestDto = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/posts/{postId}/reserve", id)
                                .content(jsonRequestDto)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("예약보기")
    void showReservedPostSuccessTest() throws Exception {
        Long id = 1L;
        Member member = new Member();
        ReservationResponseDto response = new ReservationResponseDto(new Post(), new Reservation());
        doReturn(response).when(reservationService).showReservedPost(id, member);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/posts/{postId}/reserve", id))
                                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("예약 취소하기")
    void cancelReservedPostSuccessTest() throws Exception {
        Long id = 1L;
        Member member = new Member();
        doNothing().when(reservationService).cancelReservePost(id, member);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/posts/{postId}/reserve", id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("예약하고 싶은 게시물 예약날짜 보기")
    void showReservedDateSuccessTest() throws Exception {
        Long id = 1L;
        Member member = new Member();
        List<ReservationResponseDto> response = new ArrayList<ReservationResponseDto>();
        doReturn(response).when(reservationService).showReservedDate(id, member);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/posts/{postId}/reserve", id))
                .andExpect(status().isOk());
    }

}