package com.team11.shareoffice.reservation.validator;

import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.util.ErrorCode;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import com.team11.shareoffice.reservation.dto.ReservationRequestDto;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final PostRepository postRepository;
    private final ReservationRepository reservationRepository;

    public Post validateIsExistPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST));
    }

    public void validateReserveDate(Post post, ReservationRequestDto requestDto){
        List<Reservation> reservationList = reservationRepository.findAllByPostReserved(post, requestDto.getStartDate(), requestDto.getEndDate());
        if(reservationList.size()>0){
            throw new CustomException(ErrorCode.INVALID_DATE);
        }
    }

    public Reservation validateReservation(Post post, Member member){
        return reservationRepository.findByMemberAndPost(member, post).orElseThrow( () -> new CustomException(ErrorCode.NOT_RESERVED) );
    }

}
