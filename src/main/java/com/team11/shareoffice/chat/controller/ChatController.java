package com.team11.shareoffice.chat.controller;

import com.team11.shareoffice.chat.dto.ChatDto;
import com.team11.shareoffice.chat.service.ChatService;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;


//    @GetMapping("/chat/room")
//    public ResponseDto findMessageHistory(@RequestParam(value = "roomId", required = false) Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return chatService.findMessageHistory(roomId, userDetails.getUser());
//    }

    @MessageMapping("/chat/message")
    public void message(ChatDto message) {
        chatService.saveMessage(message);
    }

    @DeleteMapping("/chat/room/{roomId}")
    public ResponseDto deleteRoom(@PathVariable Long roomId,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatService.deleteRoom(roomId,userDetails.getMember());
    }
}
