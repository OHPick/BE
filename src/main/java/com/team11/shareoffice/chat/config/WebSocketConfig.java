package com.team11.shareoffice.chat.config;

import com.team11.shareoffice.global.jwt.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  //STOMP 사용
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;

    public WebSocketConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {  //한 클라이언트에서 다른 클라이언트로 메시지를 라우팅 하는 데 사용될 메시지 브로커를 구성
        config.enableSimpleBroker("/sub"); //받기  // 해당하는 경로를 SUBSCRIBE하는 Client에게 메세지를 전달하는 간단한 작업을 수행
        config.setApplicationDestinationPrefixes("/pub"); //보내기  // @MessageMapping 어노테이션이 달린  메서드로 라우팅됨.
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {   //웹 소켓 서버에 연결하는 데 사용할 웹 소켓 엔드 포인트를 등록
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }

//     @Override
//     public void configureClientInboundChannel(ChannelRegistration registration) {
//         // WebSocket 인터셉터 등록
//         registration.interceptors(webSocketTokenInterceptor());
//     }

//     @Bean
//     public WebSocketTokenInterceptor webSocketTokenInterceptor() {
//         return new WebSocketTokenInterceptor(jwtUtil);
//     }
}
