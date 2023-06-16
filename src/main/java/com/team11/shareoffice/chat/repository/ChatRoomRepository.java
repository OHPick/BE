package com.team11.shareoffice.chat.repository;

import com.team11.shareoffice.chat.entity.ChatRoom;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryCustom{

    Optional<ChatRoom> findChatRoomByPostAndMember(Post post, Member member);

}

