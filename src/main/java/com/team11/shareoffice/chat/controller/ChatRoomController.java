package com.team11.shareoffice.chat.controller;

import com.team11.shareoffice.chat.service.ChatService;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRoomController {

    private final ChatService chatService;

    @PostMapping("/chat/room/{postId}")
    public ResponseDto<Long> enterRoom(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatService.enterRoom(postId, userDetails.getMember());
    }


    @GetMapping("/chat/room")
    public ResponseDto<List<?>> getAllChatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatService.getAllChatRooms(userDetails.getMember());
    }
    @GetMapping("/chat/room/{roomId}")
    public ResponseDto<?> getChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatService.getChatRoom(roomId,userDetails.getMember());
    }

    @DeleteMapping("/chat/room/{roomId}")
    public ResponseDto<?> deleteRoom(@PathVariable Long roomId,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatService.deleteRoom(roomId, userDetails.getMember());
    }
}
