package com.team11.shareoffice.reservation.service;


import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.dto.ReservationRequestDto;
import com.team11.shareoffice.reservation.dto.ReservationResponseDto;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import com.team11.shareoffice.reservation.validator.ReservationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationValidator reservationValidator;

    public ReservationResponseDto showReservedPost( Long postId, Member member) {
        Post post = reservationValidator.validateIsExistPost(postId);
        Reservation reservation = reservationValidator.validateReservation(post, member);
        return new ReservationResponseDto(post,reservation);
    }


    public void reservePost(Long postId, ReservationRequestDto requestDto, Member member) {
        Post post = reservationValidator.validateIsExistPost(postId);
        reservationValidator.validateReserveDate(post,requestDto);
        Reservation newReserve = new Reservation(member, post, requestDto.getStartDate(), requestDto.getEndDate());
        reservationRepository.save(newReserve);
    }

    public void cancelReservePost(Long postId, Member member) {
        Post post = reservationValidator.validateIsExistPost(postId);
        Reservation reservation = reservationValidator.validateReservation(post,member);
        reservationRepository.delete(reservation);
    }

    public List<ReservationResponseDto> showReservedDate( Long postId, Member member) {
        Post post = reservationValidator.validateIsExistPost(postId);

        return reservationRepository.findAllByPost(post).stream().map(ReservationResponseDto::new).toList();
    }

}
