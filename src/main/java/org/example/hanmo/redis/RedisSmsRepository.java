package org.example.hanmo.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class RedisSmsRepository {
    private final String PREFIX_SMS_KEY = "sms:";
    private final String VERIFIED_SUFFIX = "verified:";
    private final RedisTemplate<String, String> redisTemplate;

    // SMS 인증 코드 생성 (TTL 5분)
    public void createSmsCertification(String phoneNumber, String code){
        int LIMIT_TIME = 5 * 60;
        redisTemplate.opsForValue().set(PREFIX_SMS_KEY + phoneNumber, code, Duration.ofSeconds(LIMIT_TIME));
    }

    // SMS 인증 코드 조회
    public String getSmsCertification(String phoneNumber){
        return redisTemplate.opsForValue().get(PREFIX_SMS_KEY + phoneNumber);
    }

    public void deleteSmsCertification(String phoneNumber){
        redisTemplate.delete(PREFIX_SMS_KEY + phoneNumber);
    }

    public boolean hasKey(String phoneNumber){
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX_SMS_KEY + phoneNumber));
    }

    public void setVerifiedFlag(String phoneNumber) {
        String key = PREFIX_SMS_KEY + VERIFIED_SUFFIX + phoneNumber;
        int FLAG_TTL = 5 * 60;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, "true", Duration.ofSeconds(FLAG_TTL));
    }

    public boolean isVerifiedFlag(String phoneNumber) {
        String key = PREFIX_SMS_KEY + VERIFIED_SUFFIX + phoneNumber;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteVerifiedFlag(String phoneNumber) {
        String key = PREFIX_SMS_KEY + VERIFIED_SUFFIX + phoneNumber;
        redisTemplate.delete(key);
    }
}
