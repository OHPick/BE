package com.team11.shareoffice.reservation.service;


import com.team11.shareoffice.chat.entity.ChatMessage;
import com.team11.shareoffice.chat.entity.ChatRoom;
import com.team11.shareoffice.chat.repository.ChatMessageRepository;
import com.team11.shareoffice.chat.repository.ChatRoomRepository;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.dto.ReservationRequestDto;
import com.team11.shareoffice.reservation.dto.ReservationResponseDto;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import com.team11.shareoffice.reservation.validator.ReservationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReservationValidator reservationValidator;
    private final ChatRoomRepository chatRoomRepository;

    public ReservationResponseDto showReservedPost( Long postId, Long reservationId, Member member) {
        Post post = reservationValidator.validateIsExistPost(postId);
        Reservation reservation = reservationValidator.validateIsExistReservation(reservationId);
        reservationValidator.validateReservation(post, reservation, member);
        return new ReservationResponseDto(post,reservation);
    }


    public Long reservePost(Long postId, ReservationRequestDto requestDto, Member member) {
        Post post = reservationValidator.validateIsExistPost(postId);
        reservationValidator.validateReserveDate(post,requestDto);
        Reservation newReserve = new Reservation(member, post, requestDto.getStartDate(), requestDto.getEndDate());
        reservationRepository.save(newReserve);

        ChatRoom room = chatRoomRepository.findChatRoomByPostAndMember(post, member).orElse(null);
        if (room == null) {
            room = new ChatRoom(post, member, post.getMember());
            chatRoomRepository.saveAndFlush(room);
        }

        ChatMessage chatMessage = new ChatMessage(post.getMember(), "안녕하세요. 예약해주셔서 감사합니다. 추후에 예약 확인 후 안내해드리겠습니다.", room);
        chatMessageRepository.saveAndFlush(chatMessage);

        return newReserve.getId();
    }

    public void cancelReservePost(Long postId, Long reservationId, Member member) {
        Post post = reservationValidator.validateIsExistPost(postId);
        Reservation reservation = reservationValidator.validateIsExistReservation(reservationId);
        reservationValidator.validateReservation(post, reservation, member);
        reservationRepository.delete(reservation);
    }

    public List<ReservationResponseDto> showReservedDate( Long postId, Member member) {
        Post post = reservationValidator.validateIsExistPost(postId);

        return reservationRepository.findAllByPost(post).stream().map(ReservationResponseDto::new).toList();
    }

}
