package com.team11.shareoffice.reservation.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.entity.QReservation;
import com.team11.shareoffice.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepositoryImpl extends QuerydslRepositorySupport implements ReservationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ReservationRepositoryImpl(JPAQueryFactory jpaQueryFactory){
        super(Reservation.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Reservation> findAllByPostReservedAndNotFinished (Post post, LocalDate startDate, LocalDate endDate){
        QReservation r = QReservation.reservation;
        return jpaQueryFactory
                .select(r)
                .from(r)
                .where(r.post.id.eq(post.getId())
                        .and(r.endDate.after(startDate))
                        .and(r.startDate.before(endDate))
                        .and(r.isFinished.eq(false)))
                .fetchAll()
                .fetch();
    }

    @Override
    public Optional<Reservation> findByMemberAndPostAndNotFinished(Member member, Post post){
        QReservation r = QReservation.reservation;
        return  Optional.ofNullable(jpaQueryFactory
                .select(r)
                .from(r)
                .where(r.post.id.eq(post.getId())
                        .and(r.member.id.eq(member.getId()))
                        .and(r.isFinished.eq(false)))
                .fetchOne());
    }
}
