package com.team11.shareoffice.reservation.repository;

import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepositoryCustom {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Reservation> findAllByPostReservedAndNotFinished ( Post post, LocalDate startDate,LocalDate endDate);

    Optional<Reservation> findByMemberAndPostAndNotFinished(Member member, Post post);
}
