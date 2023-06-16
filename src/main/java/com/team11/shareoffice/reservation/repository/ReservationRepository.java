package com.team11.shareoffice.reservation.repository;


import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {

    List<Reservation> findAllByMemberOrderByStartDateAsc(Member member);
    List<Reservation> findAllByPost(Post post);
}
