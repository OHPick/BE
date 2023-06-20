package com.team11.shareoffice.chat.dto;


import lombok.*;

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
