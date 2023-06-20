package com.team11.shareoffice.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {

    private Long roomId;
    private String title;
    private String postImage;
    private String message;
    private String createdAt;
    private int notSeenCount;

}
