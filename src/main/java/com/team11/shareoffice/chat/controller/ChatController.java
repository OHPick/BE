//package com.team11.shareoffice.chat.controller;
//
//
//import com.team11.shareoffice.chat.dto.ChatDto;
//import com.team11.shareoffice.chat.service.ChatService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//@RestController
//@Slf4j
//@RequiredArgsConstructor
//public class ChatController {
//
//    private final ChatService chatService;
//    private final SimpMessagingTemplate msgOperation;
//
//
//    @MessageMapping("/chat/enter")
//    @SendTo("/sub/chat/room")
//    public void enterChatRoom(ChatDto chatDto, SimpMessageHeaderAccessor headerAccessor) throws Exception {
//        Thread.sleep(500); // simulated delay
//        ChatDto newchatdto = chatService.enterChatRoom(chatDto, headerAccessor);
//        msgOperation.convertAndSend("/sub/chat/room", newchatdto);
//    }
//
//    @MessageMapping("/chat/send")
//    public void sendChatRoom(@RequestBody ChatDto chatDto) throws Exception {
//        Thread.sleep(500); // simulated delay
//        ChatDto newchatDto = badWordFiltering.change(chatDto);
//        newchatDto.setChatUserList(chatService.getChatUserList());
//        msgOperation.convertAndSend("/sub/chat/room", newchatDto);
//    }
//
//    @EventListener
//    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        ChatDto chatDto = chatService.disconnectChatRoom(headerAccessor);
//        msgOperation.convertAndSend("/sub/chat/room", chatDto);
//    }
//}