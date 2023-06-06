package com.team11.shareoffice.chat.controller;

import com.team11.shareoffice.chat.dto.ChatDto;
import com.team11.shareoffice.chat.service.ChatService;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(ChatDto message) {
        chatService.saveMessage(message);
    }
}
