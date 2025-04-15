package org.example.hanmo.redis;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class RedisSmsRepository {
    private final String PREFIX_SMS_KEY = "sms:";
    private final String VERIFIED_SUFFIX = "verified:";
    private final String SMS_CODE_PREFIX = "smsCode:";

    // 복구용 SMS Hash입니다.
    private final String RESTORE_SMS_CODE_PREFIX = "restoreSms:";
    private final RedisTemplate<String, String> redisTemplate;

    // SMS 인증 코드 생성 (TTL 5분)
    public void createSmsCertification(String phoneNumber, String code) {
        int LIMIT_TIME = 5 * 60;
        redisTemplate
                .opsForValue()
                .set(SMS_CODE_PREFIX + code, phoneNumber, Duration.ofSeconds(LIMIT_TIME));
    }

    // SMS 인증 코드 조회
    public String getSmsCertification(String code) {
        return redisTemplate.opsForValue().get(SMS_CODE_PREFIX + code);
    }

    public void deleteSmsCertification(String code) {
        redisTemplate.delete(SMS_CODE_PREFIX + code);
    }

    public boolean hasKey(String phoneNumber) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(PREFIX_SMS_KEY + VERIFIED_SUFFIX + phoneNumber));
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

    // 복구용 코드 복구는 3분내로 입력하도록 규정함
    public void createRestoreSmsCertification(String phoneNumber, String code) {
        int LIMIT_TIME = 3 * 60; // 3분
        redisTemplate
                .opsForValue()
                .set(RESTORE_SMS_CODE_PREFIX + code, phoneNumber, Duration.ofSeconds(LIMIT_TIME));
    }

    public String getRestoreSmsCertification(String code) {
        return redisTemplate.opsForValue().get(RESTORE_SMS_CODE_PREFIX + code);
    }

    public void deleteRestoreSmsCertification(String code) {
        redisTemplate.delete(RESTORE_SMS_CODE_PREFIX + code);
    }
}
