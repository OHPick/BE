package com.team11.shareoffice.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  //STOMP 사용
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {  //한 클라이언트에서 다른 클라이언트로 메시지를 라우팅 하는 데 사용될 메시지 브로커를 구성
        config.enableSimpleBroker("/sub"); //받기
        config.setApplicationDestinationPrefixes("/pub"); //보내기
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {   //웹 소켓 서버에 연결하는 데 사용할 웹 소켓 엔드 포인트를 등록
        registry.addEndpoint("/ws").setAllowedOrigins("*");
    }
}
