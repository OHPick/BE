package com.team11.shareoffice.chat.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
        QChatMessage lastMessageSubQuery = QChatMessage.chatMessage;

        List<ChatMessage> latestMessages = jpaQueryFactory  // latestMessages : 최신 메시지들 저장리스트
            .selectFrom(lastMessageSubQuery)
            .where(lastMessageSubQuery.id.in(
                JPAExpressions.select(chatMessageSubQuery.id.max())  // (하위쿼리) room.id별로 메시지id의 최댓값 (즉,최신메세지)
                    .from(chatMessageSubQuery)
                    .groupBy(chatMessageSubQuery.room.id)))
            .fetch();
        return jpaQueryFactory
            .select(Projections.fields(  // 채팅방목록에 보여줄  제목,이미지,마지막메시지
                ChatRoomResponseDto.class,
                chatRoom.id.as("roomId"),
                post.title,
                post.postImage,
                chatMessageSubQuery.message,
                chatMessageSubQuery.createdAt
                             ))
            .from(chatRoom) // 채팅방으로부터
            .leftJoin(post).on(chatRoom.post.id.eq(post.id))  // 이미지를 위해 조인
            .leftJoin(chatMessageSubQuery).on(chatRoom.id.eq(chatMessageSubQuery.room.id))  // 마지막메시지를위해 조인
            .where(chatRoom.member.id.eq(member.getId()).or(chatRoom.owner.id.eq(member.getId())))   // 로그인한 사람이 사용중인 채팅방인  ( 채팅방멤버와 로그인한사람이 일치하는 )
            .groupBy(chatRoom.id)  // 채팅방id 별로
            .fetch()
                .stream()
                .peek(dto -> {   // peek : 봔환된결과를 처리하기위해  // dto : ChatRoomResponseDto 객체를 의미.
                    Optional<ChatMessage> latestMessage = latestMessages.stream()
                            .filter(message -> message.getRoom().getId().equals(dto.getRoomId()))  // 최신 메시지들 중에서 현재 처리 중인 채팅방과 일치하는 것을 필터
                            .findFirst();  // 필터링된 최신 메시지 중 첫 번째 요소를 가져옴
                    latestMessage.ifPresent(message -> dto.setMessage(message.getMessage()));// 최신 메시지가 존재하는 경우 해당 메시지의 내용을 ChatRoomResponseDto의 message 필드에 설정
                    latestMessage.ifPresent(message -> dto.setCreatedAt(message.getCreatedAt()));// 최신 메시지가 존재하는 경우 해당 메시지의 시간을 ChatRoomResponseDto의 createAt 필드에 설정
                })
                .toList();
    }

}
