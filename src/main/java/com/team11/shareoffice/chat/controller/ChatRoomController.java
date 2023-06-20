package com.team11.shareoffice.chat.controller;

import com.team11.shareoffice.chat.service.ChatService;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRoomController {

    private final ChatService chatService;

    @Operation(summary = "채팅방 생성하기 API")
    @PostMapping("/chat/room/{postId}")
    public ResponseDto<Long> createRoom(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseDto.setSuccess("방 입장",chatService.createRoom(postId, userDetails.getMember()));
    }

    @Operation(summary = "모든 채팅방 불러오기 API")
    @GetMapping("/chat/room")
    public ResponseDto<List<?>> getAllChatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseDto.setSuccess(chatService.getAllChatRooms(userDetails.getMember()));
    }
    @Operation(summary = "채팅방 들어가기 API")
    @GetMapping("/chat/room/{roomId}")
    public ResponseDto<?> getChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseDto.setSuccess(chatService.getChatRoom(roomId,userDetails.getMember()));
    }

    @Operation(summary = "채팅방 삭제 API")
    @DeleteMapping("/chat/room/{roomId}")
    public ResponseDto<?> deleteRoom(@PathVariable Long roomId,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatService.deleteRoom(roomId, userDetails.getMember());
        return ResponseDto.setSuccess("채팅방 삭제 성공");
    }
}
