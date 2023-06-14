package com.team11.shareoffice.chat.validator;

import com.team11.shareoffice.chat.entity.ChatRoom;
import com.team11.shareoffice.chat.repository.ChatRoomRepository;
import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.util.ErrorCode;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatValidator {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;


    public Post validatePost(Long postId){
        return postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST));
    }


    public Member validateMember(String nickname){
        return memberRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER));
    }

    public ChatRoom validateRoomId(Long roomId){
        return chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
    }

    public void validateChatRoomMember(ChatRoom room, Member member){
        if(!(room.getMember().getId().equals(member.getId())) && !(room.getOwner().getId().equals(member.getId()))){
            throw new CustomException(ErrorCode.INVALID_CHAT_MEMBER);
        }
    }
}
