package com.team11.shareoffice.chat.repository;

import com.team11.shareoffice.chat.dto.ChatRoomResponseDto;
import com.team11.shareoffice.member.entity.Member;

import java.util.List;

public interface ChatRoomRepositoryCustom {
    List<ChatRoomResponseDto> findAllChatRoom(Member member);
}
