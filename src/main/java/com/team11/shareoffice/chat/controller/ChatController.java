package com.team11.shareoffice.chat.controller;

import com.team11.shareoffice.chat.dto.ChatDto;
import com.team11.shareoffice.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    @Operation(summary = "메세지 API")
    @MessageMapping("/chat/message")
    public void message(ChatDto message) {
        chatService.saveMessage(message);
    }
}
