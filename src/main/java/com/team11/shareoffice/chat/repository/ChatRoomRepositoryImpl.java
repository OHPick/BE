package com.team11.shareoffice.chat.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team11.shareoffice.chat.dto.ChatResponseDto;
import com.team11.shareoffice.chat.dto.ChatRoomResponseDto;
import com.team11.shareoffice.chat.entity.ChatMessage;
import com.team11.shareoffice.chat.entity.ChatRoom;
import com.team11.shareoffice.chat.entity.QChatMessage;
import com.team11.shareoffice.chat.entity.QChatRoom;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.QPost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ChatRoomRepositoryImpl extends QuerydslRepositorySupport implements ChatRoomRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;


    public ChatRoomRepositoryImpl(JPAQueryFactory queryFactory) {
        super(ChatRoom.class);
        this.jpaQueryFactory = queryFactory;
    }
    @Override
    public List<ChatRoomResponseDto> findAllChatRoom(Member member) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QPost post = QPost.post;
        QChatMessage chatMessageSubQuery = QChatMessage.chatMessage;

        List<ChatMessage> latestMessages = jpaQueryFactory
            .selectFrom(chatMessageSubQuery)
            .where(chatMessageSubQuery.id.in(
                JPAExpressions.select(chatMessageSubQuery.id.max())
                    .from(chatMessageSubQuery)
                    .groupBy(chatMessageSubQuery.room.id)))
            .fetch();

        return jpaQueryFactory
            .select(Projections.fields(
                ChatRoomResponseDto.class,
                chatRoom.id,
                post.title,
                post.postImage,
                chatMessageSubQuery.message
                             ))
            .from(chatRoom)
            .leftJoin(post).on(chatRoom.post.id.eq(post.id))
            .leftJoin(chatMessageSubQuery).on(chatRoom.id.eq(chatMessageSubQuery.room.id))
            .where(chatRoom.member.id.eq(member.getId()))
            .groupBy(chatRoom.id)
            .fetch()
                .stream()
                .peek(dto -> {
                    Optional<ChatMessage> latestMessage = latestMessages.stream()
                            .filter(message -> message.getRoom().getId().equals(dto.getId()))
                            .findFirst();
                    latestMessage.ifPresent(message -> dto.setMessage(message.getMessage()));
                })
                .toList();
    }

}
