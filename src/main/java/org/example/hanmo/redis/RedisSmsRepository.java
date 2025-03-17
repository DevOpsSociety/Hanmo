package org.example.hanmo.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class RedisSmsRepository {
    private final String PREFIX_SMS_KEY = "sms:";
    private final RedisTemplate<String, String> redisTemplate;

    // 인증 정보 생성 메서드
    public void createSmsCertification(String phoneNumber, String code){
        int LIMIT_TIME = 3 * 60;
        redisTemplate.opsForValue()
                .set(PREFIX_SMS_KEY + phoneNumber, code, Duration.ofSeconds(LIMIT_TIME));
    }

    // 인증 정보 가져오는,
    public String getSmsCertification(String phoneNumber){
        return redisTemplate.opsForValue().get(PREFIX_SMS_KEY + phoneNumber);
    }

    // 인증 정보 삭제
    public void deleteSmsCertification(String phoneNumber){
        redisTemplate.delete(PREFIX_SMS_KEY + phoneNumber);
    }

    // 키 존재 여부 확인
    public boolean hasKey(String phoneNumber){
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX_SMS_KEY + phoneNumber));
    }

}
