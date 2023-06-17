package com.team11.shareoffice.post.validator;

import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.util.ErrorCode;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final PostRepository postRepository;
    private final ReservationRepository reservationRepository;

    public Post validateIsExistPost(Long id){
        return postRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST));
    }

    public void validatePostAuthor(Post post, Member member){
        if(!post.getMember().getEmail().equals(member.getEmail())){
            throw new IllegalArgumentException("게시글의 작성자만 수정 / 삭제가 가능합니다.");
        }
    }
    public void unfinishedReservationCheck(Post post){
        List<Reservation> reservations = reservationRepository.findAllByPostAndIsFinishedFalse(post);
        if (!reservations.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FINISHED_RESERVATION_EXIST);
        }
    }


}
