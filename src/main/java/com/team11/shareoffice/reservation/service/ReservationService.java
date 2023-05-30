package com.team11.shareoffice.reservation.service;


import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.dto.ReservationRequestDto;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import com.team11.shareoffice.reservation.validator.ReservationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationValidator reservationValidator;

    public ResponseDto<?> reservePost(Long postId, ReservationRequestDto requestDto, Member member) {
        Post post = reservationValidator.validateIsExistPost(postId);
        reservationValidator.validateReserveDate(post,requestDto);
        Reservation newReserve = new Reservation(member, post, requestDto.getStartDate(), requestDto.getEndDate());
        reservationRepository.save(newReserve);
        return ResponseDto.setSuccess(null);
    }

    public ResponseDto<?> cancelReservePost(Long postId, Member member) {
        Post post = reservationValidator.validateIsExistPost(postId);
        Reservation reservation = reservationValidator.validateReservation(post,member);
        reservationRepository.delete(reservation);
        return ResponseDto.setSuccess(null);
    }

}
