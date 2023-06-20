package com.team11.shareoffice.chat.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team11.shareoffice.chat.dto.ChatResponseDto;
import com.team11.shareoffice.chat.dto.ChatRoomResponseDto;
import com.team11.shareoffice.chat.entity.ChatMessage;
import com.team11.shareoffice.chat.entity.ChatRoom;
import com.team11.shareoffice.chat.entity.QChatMessage;
import com.team11.shareoffice.chat.entity.QChatRoom;
import com.team11.shareoffice.like.entity.Likes;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.dto.MainPageResponseDto;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.entity.QPost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        QChatMessage notSeenMessageSubQuery = QChatMessage.chatMessage;

        // 각 채팅 방 마다 마지막으로 보낸 메세지
        List<ChatMessage> latestMessages = jpaQueryFactory  // latestMessages : 최신 메시지들 저장리스트
            .selectFrom(lastMessageSubQuery)
            .where(lastMessageSubQuery.id.in(
                JPAExpressions.select(chatMessageSubQuery.id.max())  // (하위쿼리) room.id별로 메시지id의 최댓값 (즉,최신메세지)
                    .from(chatMessageSubQuery)
                    .groupBy(chatMessageSubQuery.room.id)))
            .fetch();


        QueryResults<Tuple> results  = jpaQueryFactory
                .select(
                        chatRoom.id.as("roomId"),
                        post.title,
                        post.postImages.as("postImage")
                )
                .from(chatRoom) // 채팅방으로부터
                .leftJoin(post).on(chatRoom.post.id.eq(post.id))  // 이미지를 위해 조인
                .where(chatRoom.member.id.eq(member.getId()).or(chatRoom.owner.id.eq(member.getId())))   // 로그인한 사람이 사용중인 채팅방인  ( 채팅방 멤버/주인 과 로그인한사람이 일치하는 )
                .groupBy(chatRoom.id)  // 채팅방id 별로
                .fetchResults();

        List<Tuple> resultList = results.getResults();

        return resultList.stream()
                .map(result -> {
                    List<String> postImages = result.get(2, List.class);
                    String firstImage = postImages.get(0);

                    Optional<ChatMessage> latestMessage = latestMessages.stream()
                            .filter(message -> message.getRoom().getId().equals(result.get(0, Long.class)))  // 최신 메시지들 중에서 현재 처리 중인 채팅방과 일치하는 것을 필터
                            .findFirst();  // 필터링된 최신 메시지 중 첫 번째 요소를 가져옴
                    String lastMessage = latestMessage.map(ChatMessage::getMessage).orElse(null);
                    String createdAtMessage = latestMessage.map(ChatMessage::getCreatedAt).orElse(null);

                    int notSeenMessageCount = Math.toIntExact(jpaQueryFactory  // latestMessages : 최신 메시지들 저장리스트
                            .selectFrom(notSeenMessageSubQuery)
                            .where(notSeenMessageSubQuery.room.id.eq(result.get(0, Long.class))
                                    .and(notSeenMessageSubQuery.sender.id.ne(member.getId()))
                                    .and(notSeenMessageSubQuery.isNotSeen.eq(true)))
                            .groupBy(notSeenMessageSubQuery.room.id)
                            .fetchCount());

                    return new ChatRoomResponseDto(
                            result.get(0, Long.class),
                            result.get(1, String.class),
                            firstImage,
                            lastMessage,
                            createdAtMessage,
                            notSeenMessageCount
                            );
                        }
                ).toList();
    }

}
