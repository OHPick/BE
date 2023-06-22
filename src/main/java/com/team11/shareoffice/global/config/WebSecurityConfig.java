package com.team11.shareoffice.global.config;

import com.team11.shareoffice.global.jwt.CookieUtil;
import com.team11.shareoffice.global.jwt.JwtAuthFilter;
import com.team11.shareoffice.global.jwt.JwtUtil;
import com.team11.shareoffice.global.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final RedisService redisService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
//    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // h2-console 사용 및 resources 접근 허용 설정
        return web -> web.ignoring()
                //h2 콘솔
//                .requestMatchers(PathRequest.toH2Console())
                //스웨거
                .requestMatchers("/swagger*/**", "/v3/api-docs/**")
                //static 파일들
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().httpBasic().disable();

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeHttpRequests()
                //회원가입, 로그인페이지, 메인 페이지.
                .requestMatchers("/api/members/**").permitAll()
                .requestMatchers("/oauth/kakao").permitAll()
                .requestMatchers("/api/email/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/posts/**").permitAll()
                // 채팅관련
                .requestMatchers("/ws/**").permitAll()
                .anyRequest().authenticated()
                // JWT 인증/인가를 사용하기 위한 설정
                .and().addFilterBefore(new JwtAuthFilter(jwtUtil, redisService, cookieUtil), UsernamePasswordAuthenticationFilter.class);

        // 이 설정을 해주지 않으면 밑의 cors가 적용되지 않는다
        http.cors();


        //로그아웃 기능
//        http.logout()
////                .logoutRequestMatcher(new AntPathRequestMatcher("/api/members/logout", "POST"))
////                .logoutUrl("/api/members/logout")
//                .invalidateHttpSession(true)
//                .deleteCookies(JwtUtil.ACCESS_TOKEN, JwtUtil.REFRESH_TOKEN)
//                .logoutSuccessUrl("/api/posts");

        // 401 Error 처리, Authorization 즉, 인증과정에서 실패할 시 처리
//        http.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);

        return http.build();
    }

    //나중에 cors 다룰때 참고
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOriginPattern("*");
        config.addExposedHeader(JwtUtil.ACCESS_TOKEN);
        config.addExposedHeader("Set-Cookie");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.validateAllowCredentials();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
