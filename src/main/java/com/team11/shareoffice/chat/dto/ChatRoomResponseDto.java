package com.team11.shareoffice.chat.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ChatRoomResponseDto {

    private Long id;
    private String title;
    private String postImage;
    private String message;

}
