package com.team11.shareoffice.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;

    public void setValues(String email, String refreshToken, Duration duration){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(email, refreshToken, duration);
    }

    public String getValues(String email){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(email);
    }

    public void delValues(String email){
        redisTemplate.delete(email);
    }

    public void setBlackList(String accessTokenFromRequest) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(accessTokenFromRequest, "blacklisted", Duration.ofHours(1));
    }

    public boolean isBlackListed(String accessToken) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String value = values.get(accessToken);
        return value != null && value.equals("blacklisted");
    }
}

