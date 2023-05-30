package com.team11.shareoffice.reservation.entity;

import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@RequiredArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    //대여 시작일
    @Column(nullable = false)
    private LocalDate startDate;

    //대여 종료일
    @Column(nullable = false)
    private LocalDate endDate;

    public Reservation(Member member, Post post, LocalDate startDate, LocalDate endDate) {
        this.member = member;
        this.post = post;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
