package com.team11.shareoffice.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatListResponseDto {
    private String nickname;
    private List<ChatResponseDto> chats;
}
