package com.team11.shareoffice.chat.entity;

import com.team11.shareoffice.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member sender;

    @Column(nullable = false)
    private String message;

    @ManyToOne
    private ChatRoom room;

    @Column(nullable = false)
    private String createdAt;

    @PrePersist
    private void setCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
    }

    public ChatMessage(Member sender, String message, ChatRoom room) {
        this.sender = sender;
        this.message = message;
        this.room = room;
    }
}