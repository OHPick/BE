package com.team11.shareoffice.chat.service;

import com.team11.shareoffice.chat.dto.ChatDto;
import com.team11.shareoffice.chat.dto.ChatListResponseDto;
import com.team11.shareoffice.chat.dto.ChatResponseDto;
import com.team11.shareoffice.chat.dto.ChatRoomResponseDto;
import com.team11.shareoffice.chat.entity.ChatMessage;
import com.team11.shareoffice.chat.entity.ChatRoom;
import com.team11.shareoffice.chat.repository.ChatMessageRepository;
import com.team11.shareoffice.chat.repository.ChatRoomRepository;
import com.team11.shareoffice.chat.repository.ChatRoomRepositoryImpl;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.util.ErrorCode;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessageSendingOperations template;
    private final ChatRoomRepositoryImpl chatRoomRepositoryImpl;

    @Transactional
    public Long enterRoom(Long postId, Member member) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST));
        Member owner = memberRepository.findByNickname(post.getMember().getNickname()).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER));
        ChatRoom room = chatRoomRepository.findChatRoomByPostAndMember(post, member).orElse(null);
        if (room == null) {
            room = new ChatRoom(post, member, owner);
            chatRoomRepository.saveAndFlush(room);
        }
        return room.getId();
    }

//    @Transactional
//    public ResponseDto findMessageHistory(Long roomId, Member member) {
//        return ResponseMessage.SuccessResponse("대화 불러오기 성공", chatMessageQueryRepository.findMessageList(roomId, user.getId()));
//    }

    @Transactional
    public void saveMessage(ChatDto message) {
        Member member = memberRepository.findByNickname(message.getSender()).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER));
        ChatRoom room = chatRoomRepository.findById(message.getRoomId()).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
        ChatMessage chatMessage = new ChatMessage(member, message.getMessage(), room);
        chatMessageRepository.saveAndFlush(chatMessage);
        ChatResponseDto responseDto = new ChatResponseDto(chatMessage.getRoom().getId(), chatMessage.getSender().getNickname(), chatMessage.getMessage(), changeDateFormatMessage(chatMessage.getCreatedAt()), member.getImageUrl());
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), responseDto);
    }

    @Transactional
    public void deleteRoom(Long roomId,Member member) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
        if (room.getMember().getNickname().equals(member.getNickname()) || room.getOwner().getNickname().equals(member.getNickname())){
            chatRoomRepository.deleteById(room.getId());
        }
        else {
            throw new CustomException(ErrorCode.INVALID_CHAT_MEMBER);
        }
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getAllChatRooms(Member member){
        return chatRoomRepositoryImpl.findAllChatRoom(member).stream()
                .peek(chatRoomResponseDto -> chatRoomResponseDto.setCreatedAt(changeDateFormatChatRoom(chatRoomResponseDto.getCreatedAt())))
                .toList();
    }

    @Transactional(readOnly = true)
    public ChatListResponseDto getChatRoom(Long roomId, Member member){
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow( () -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        List<ChatMessage> messages = chatMessageRepository.findAllByRoomOrderByCreatedAt(room);
        List<ChatResponseDto> chatResponseDtos = messages.stream()
                .map(message -> new ChatResponseDto(roomId, message.getSender().getNickname(), message.getMessage(), changeDateFormatMessage(message.getCreatedAt()), member.getImageUrl()))
                        .toList();

        return new ChatListResponseDto(member.getNickname(), chatResponseDtos);
    }


    private String changeDateFormatMessage(String createdAt) {
        String[] date = createdAt.split(" ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String today = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
        return date[0].equals(today) ? date[1] : date[0];
    }

    private String changeDateFormatChatRoom(String createdAt) {
        String[] date = createdAt.split(" ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
        String today = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
        LocalDate dateFormat = LocalDate.parse(date[0]);
        String convertedDate = dateFormat.format(formatter);
        return convertedDate.equals(today) ? "오늘" : convertedDate;
    }



}

