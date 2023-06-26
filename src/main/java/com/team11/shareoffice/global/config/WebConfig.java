package com.team11.shareoffice.global.config;

import com.team11.shareoffice.global.inspector.LoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoggingInterceptor requestCountInterceptor;
    //로컬환경에서 작동할 때만 주석 풀어주세요.
    //LoggingInterceptor의 결과를 콘솔 창에 출력해줍니다.
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(requestCountInterceptor);
//
//        WebMvcConfigurer.super.addInterceptors(registry);
//    }
}