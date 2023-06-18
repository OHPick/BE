package com.team11.shareoffice.global.service;

import com.team11.shareoffice.global.util.RedisKeyCode;
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
        values.set(accessTokenFromRequest, RedisKeyCode.BLACKLISTED.getKeyName(), Duration.ofHours(1));
    }

    public boolean isBlackListed(String accessToken) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String value = values.get(accessToken);
        return value != null && value.equals(RedisKeyCode.BLACKLISTED.getKeyName());
    }

    //이메일 레디스

    //인증된 상태로 설정
      public void setEmailVerified(String email){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(RedisKeyCode.EMAIL_VERIFIED.getKeyName() + email, "true", Duration.ofMinutes(3)); // 3분 후 자동 삭제
    }

    //인증됐는지 확인
    public boolean isEmailVerified(String email){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return "true".equals(values.get(RedisKeyCode.EMAIL_VERIFIED.getKeyName() + email));
    }
    //이메일 인증코드 설정
    public void setEmailAuthCode(String email, String code, Duration duration){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(RedisKeyCode.EMAIL_VERIF_CODE.getKeyName() + email, code, duration);
    }
    //주어진 이메일에 대해 인증코드 조회
    public String getEmailAuthCode(String email){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(RedisKeyCode.EMAIL_VERIF_CODE.getKeyName() + email);
    }

    public void delEmailAuthCode(String email){
        redisTemplate.delete(RedisKeyCode.EMAIL_VERIF_CODE.getKeyName() + email);
    }
}

