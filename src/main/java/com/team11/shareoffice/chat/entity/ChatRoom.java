package com.team11.shareoffice.chat.entity;

import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Member owner;

    public ChatRoom(Post post, Member member, Member owner) {
        this.roomId = UUID.randomUUID().toString().substring(0, 8);
        this.post = post;
        this.member = member;
        this.owner = owner;
    }
}