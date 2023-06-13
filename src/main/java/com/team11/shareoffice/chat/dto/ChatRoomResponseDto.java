package com.team11.shareoffice.chat.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ChatRoomResponseDto {

    private Long roomId;
    private String title;
    private String postImage;
    private String message;
    private String createdAt;

}
