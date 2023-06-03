package com.team11.shareoffice.chat.repository;

import com.team11.shareoffice.chat.entity.ChatMessage;
import com.team11.shareoffice.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByRoomOrderByCreatedAt(ChatRoom room);
}