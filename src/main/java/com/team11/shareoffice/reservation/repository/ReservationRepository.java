package com.team11.shareoffice.reservation.repository;


import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r where r.post = :post and r.endDate >= :startDate and r.startDate <= :endDate")
    List<Reservation> findAllByPostReserved (@Param("post")Post post, @Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate);

    Optional<Reservation> findByMemberAndPost(Member member, Post post);
    List<Reservation> findAllByMemberOrderByStartDateAsc(Member member);
    List<Reservation> findAllByPost(Post post);
}
