package com.team11.shareoffice.chat.controller;

import com.team11.shareoffice.chat.dto.ChatDto;
import com.team11.shareoffice.chat.service.ChatService;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/room/{postId}/{nickname}")
    public ResponseDto enterRoom(@PathVariable Long postId, @PathVariable String nickname) {
        return chatService.enterRoom(postId, nickname);
    }

//    @GetMapping("/room")
//    public ResponseDto findMessageHistory(@RequestParam(value = "roomId", required = false) Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return chatService.findMessageHistory(roomId, userDetails.getUser());
//    }

    @MessageMapping(value = "/message")
    public void message(ChatDto message) {
        chatService.saveMessage(message);
    }

//    @DeleteMapping("/room/{roomId}")
//    public ResponseDto deleteRoom(@PathVariable Long roomId) {
//        return chatService.deleteRoom(roomId);
//    }
}
