package com.team11.shareoffice.chat.repository;

import com.team11.shareoffice.chat.entity.ChatRoom;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
//    Optional<ChatRoom> findByRoomId(String roomId);
    List<ChatRoom> findAllByMember(Member member);
    Optional<ChatRoom> findChatRoomByPostAndMember(Post post, Member member);

//    @Modifying
//    @Query(value = "update chat_room r " +
//            "left join chat_message on r.id = chat_message.room_id " +
//            "set r.is_deleted = true, " +
//            "chat_message.is_deleted = true " +
//            "where r.id = :id", nativeQuery = true)
//    void deleteAllAboutRoomById(@Param("id") Long id);
}

