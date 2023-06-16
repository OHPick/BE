package com.team11.shareoffice.reservation.validator;

import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.util.ErrorCode;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Amenities;
import com.team11.shareoffice.post.entity.OperatingTime;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.dto.ReservationRequestDto;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ReservationValidatorTest {

    @InjectMocks
    private ReservationValidator reservationValidator;

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("정상 케이스")
    void validateReserveDate_SuccessTest() {
        //given
        Member member = new Member();
        Post post = new Post();
        LocalDate startDate1 = LocalDate.of(2023, 6, 13);
        LocalDate endDate1 = LocalDate.of(2023, 6, 22);
        LocalDate startDate2 = LocalDate.of(2023, 6, 24);
        LocalDate endDate2 = LocalDate.of(2023, 6, 26);
        ReservationRequestDto requestDto = new ReservationRequestDto(startDate2, endDate2);
        List<Reservation> reservationList = new ArrayList<>();
        Reservation reservation = new Reservation();
        reservation.setPost(post);
        reservation.setMember(member);
        reservation.setStartDate(startDate1);
        reservation.setEndDate(endDate1);
        // Set other necessary fields for the reservation
        reservationList.add(reservation);
        reservationRepository.saveAll(reservationList);

        //when
        Mockito.when(reservationRepository.findAllByPostReservedAndNotFinished(Mockito.eq(post), Mockito.eq(startDate2), Mockito.eq(endDate2)))
                .thenReturn(new ArrayList<>());

        //then
        assertDoesNotThrow(() -> {
            // Call the method under test
            reservationValidator.validateReserveDate(post, requestDto);
        });
    }

    @Test
    @DisplayName("에러 케이스1 : 올바르지 않은 날짜")
    void validateReserveDate_InvalidDateExceptionTest() {
        //given
        Post post = new Post();
        LocalDate startDate = LocalDate.of(2023, 6, 13);
        LocalDate endDate = LocalDate.of(2023, 6, 22);
        ReservationRequestDto requestDto = new ReservationRequestDto(startDate, endDate);

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationValidator.validateReserveDate(post, requestDto);
        });

        //then
        assertEquals(ErrorCode.INVALID_DATE.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("에러 케이스2 : 이미 예약된 날짜")
    void validateReserveDate_ExistReserveDateExceptionTest() {
        //given
        Member member = new Member();
        Amenities amenities = new Amenities();
        OperatingTime operatingTime = new OperatingTime();
        Post post = new Post(1L, member, "title", "content", "location", 100, 10, "contentDetails", 0, new ArrayList<>(), amenities, operatingTime, false);
        LocalDate startDate = LocalDate.of(2023, 6, 20);
        LocalDate endDate = LocalDate.of(2023, 6, 22);
        ReservationRequestDto requestDto = new ReservationRequestDto(startDate, endDate);
        List<Reservation> reservationList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Reservation reservation = new Reservation();
            reservation.setPost(post);
            reservation.setMember(member);
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            // Set other necessary fields for the reservation
            reservationList.add(reservation);
        }
        reservationRepository.saveAll(reservationList);

        //when
        Mockito.when(reservationRepository.findAllByPostReservedAndNotFinished(Mockito.eq(post), Mockito.eq(startDate), Mockito.eq(endDate)))
                .thenReturn(reservationList);

        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationValidator.validateReserveDate(post, requestDto);
        });

        //then
        assertEquals(ErrorCode.EXIST_RESERVE_DATE.getMessage(), exception.getMessage());
    }

}