package com.team11.shareoffice.chat.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
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
import java.util.Objects;
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
        QChatMessage chatMessage = QChatMessage.chatMessage;

        QueryResults<Tuple> results  = jpaQueryFactory
                .select(
                        chatRoom.id.as("roomId"),
                        post.title,
                        post.postImages.as("postImage")
                )
                .from(chatRoom) // 채팅방으로부터
                .leftJoin(chatRoom.post, post)
                .where(chatRoom.member.id.eq(member.getId()).or(chatRoom.owner.id.eq(member.getId())))   // 로그인한 사람이 사용중인 채팅방인  ( 채팅방 멤버/주인 과 로그인한사람이 일치하는 )
                .groupBy(chatRoom.id)  // 채팅방id 별로
                .fetchResults();

        List<Tuple> resultList = results.getResults();

        return resultList.stream()
                .map(result -> {
                    List<String> postImages = result.get(2, List.class);
                    String firstImage = postImages.get(0);

                    // 각 채팅 방 마다 마지막으로 보낸 메세지
                    Tuple latestMessage = jpaQueryFactory  // latestMessages : 최신 메시지들 저장리스트
                            .select(
                                    chatMessage.message.as("message"),
                                    chatMessage.createdAt.as("createdAt")
                            )
                            .from(chatMessage)
                            .where(chatMessage.id.in(
                                    JPAExpressions.select(chatMessage.id.max())  // (하위쿼리) room.id별로 메시지id의 최댓값 (즉,최신메세지)
                                            .from(chatMessage)
                                            .where(chatMessage.room.id.eq(result.get(0, Long.class)))))
                            .fetchOne();
                    if (latestMessage == null) {
                        return null; // Skip this result if lastMessage is null
                    }
                    String lastMessage = latestMessage.get(0,String.class);
                    String createdAtMessage = latestMessage.get(1, String.class);



                    int notSeenMessageCount = Math.toIntExact(jpaQueryFactory  // latestMessages : 최신 메시지들 저장리스트
                            .selectFrom(chatMessage)
                            .where(chatMessage.room.id.eq(result.get(0, Long.class))
                                    .and(chatMessage.sender.id.ne(member.getId()))
                                    .and(chatMessage.isNotSeen.eq(true)))
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
