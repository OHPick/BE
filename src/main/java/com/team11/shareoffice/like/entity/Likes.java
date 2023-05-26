package com.team11.shareoffice.like.entity;

import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private boolean likeStatus;

    public Likes(Post post, Member member) {
        this.post = post;
        this.member = member;
    }

    public static Likes addLike(Member member, Post post) {
        return new Likes(post, member);
    }

    public void setLikeStatus() {
        this.likeStatus = !(this.likeStatus);
    }
}
