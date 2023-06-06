package com.team11.shareoffice.chat.config;

import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.jwt.JwtUtil;
import com.team11.shareoffice.global.util.ErrorCode;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component("customWebSocketTokenInterceptor")  // 빈 주입 동일이름 방지
public class WebSocketTokenInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketTokenInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {  //WebSocket 메시지가 전송되기 전에 호출
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);   // 메시지의 헤더에 접근
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // CONNECT 메시지인 경우 토큰 검증을 수행합니다.
            String token = accessor.getFirstNativeHeader("ACCESS_TOKEN");
            if (token != null && jwtUtil.validateToken(token)) {
                // 토큰이 유효한 경우 연결을 허용합니다.
                return message;
            } else {
                // 토큰이 유효하지 않은 경우 연결을 거부합니다.
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }
        }
        return message;
    }
}
