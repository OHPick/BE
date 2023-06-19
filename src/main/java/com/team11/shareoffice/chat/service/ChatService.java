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
import com.team11.shareoffice.chat.validator.ChatValidator;
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
import java.util.Comparator;
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
    private final ChatValidator chatValidator;

    @Transactional
    public Long enterRoom(Long postId, Member member) {
        Post post = chatValidator.validatePost(postId);
        Member owner = post.getMember();
        ChatRoom room = chatRoomRepository.findChatRoomByPostAndMember(post, member).orElse(null);
        if (room == null) {
            room = new ChatRoom(post, member, owner);
            chatRoomRepository.saveAndFlush(room);
        }
        return room.getId();
    }

    @Transactional
    public void saveMessage(ChatDto message) {
        Member member = chatValidator.validateMember(message.getSender());
        ChatRoom room = chatValidator.validateRoomId(message.getRoomId());
        chatValidator.validateChatRoomMember(room,member);
        ChatMessage chatMessage = new ChatMessage(member, message.getMessage(), room);
        chatMessageRepository.saveAndFlush(chatMessage);
        ChatResponseDto responseDto = new ChatResponseDto(chatMessage.getRoom().getId(), chatMessage.getSender().getNickname(), chatMessage.getMessage(), changeDateFormatMessage(chatMessage.getCreatedAt()), member.getImageUrl(), chatMessage.getIsSeen());
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), responseDto);
    }

    @Transactional
    public void deleteRoom(Long roomId,Member member) {
        ChatRoom room = chatValidator.validateRoomId(roomId);
        chatValidator.validateChatRoomMember(room,member);
        chatRoomRepository.deleteById(room.getId());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getAllChatRooms(Member member){
        return chatRoomRepositoryImpl.findAllChatRoom(member)
                .stream()
                .peek(chatRoomResponseDto -> chatRoomResponseDto.setCreatedAt(changeNullDateFormatChatRoom(chatRoomResponseDto.getCreatedAt())))
                .sorted(Comparator.comparing(ChatRoomResponseDto::getCreatedAt).reversed())
                .peek(chatRoomResponseDto -> chatRoomResponseDto.setCreatedAt(changeDateFormatChatRoom(chatRoomResponseDto.getCreatedAt())))
                .toList();
    }

    @Transactional
    public ChatListResponseDto getChatRoom(Long roomId, Member member){
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow( () -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
        chatValidator.validateChatRoomMember(room,member);
        List<ChatMessage> messages = chatMessageRepository.findAllByRoomOrderByCreatedAt(room).stream()
                .peek( chatMessage -> {
                            if(!(chatMessage.getSender().getId().equals(member.getId()))){chatMessage.updateIsSeen();}
                }).toList();
        List<ChatResponseDto> chatResponseDtos = messages.stream()
                .map(message -> new ChatResponseDto(roomId, message.getSender().getNickname(), message.getMessage(), changeDateFormatMessage(message.getCreatedAt()), message.getSender().getImageUrl(), message.getIsSeen()))
                        .toList();

        return new ChatListResponseDto(member.getNickname(), room.getOwner().getNickname(), chatResponseDtos);
    }



    private String changeDateFormatMessage(String createdAt) {
        String[] date = createdAt.split(" ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String today = getDateTimeNow(formatter);
        return date[0].equals(today) ? date[1] : date[0];
    }

    private String changeDateFormatChatRoom(String createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
        String today = getDateTimeNow(formatter);
        if(createdAt == null){
            return today;
        }
        String[] date = createdAt.split(" ");
        LocalDate dateFormat = LocalDate.parse(date[0]);
        String convertedDate = dateFormat.format(formatter);
        return convertedDate.equals(today) ? date[1] : convertedDate;
    }

    private String changeNullDateFormatChatRoom(String createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String now = getDateTimeNow(formatter);
        if(createdAt == null){
            return now;
        }
        return createdAt;
    }

    private String getDateTimeNow (DateTimeFormatter formatter){
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
    }


}

