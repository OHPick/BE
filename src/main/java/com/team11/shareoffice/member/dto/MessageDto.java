package com.team11.shareoffice.member.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MessageDto {
    String message;

    public MessageDto(String message) {
        this.message = message;
    }
}
