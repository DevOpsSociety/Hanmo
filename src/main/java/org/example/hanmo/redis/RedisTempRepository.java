package org.example.hanmo.redis;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RedisTempRepository {
    private static final String TEMP_TOKEN_PREFIX = "tempToken:";
    private static final String TEMP_TOKEN_LOOKUP_PREFIX = "tempTokenLookup:";

    private final RedisTemplate<String, String> redisTemplate;

    public RedisTempRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setTempToken(String phoneNumber, String token, long ttlSeconds) {
        String tokenKey = TEMP_TOKEN_PREFIX + phoneNumber;
        String lookupKey = TEMP_TOKEN_LOOKUP_PREFIX + token;
        Duration ttl = Duration.ofSeconds(ttlSeconds);
        redisTemplate.opsForValue().set(tokenKey, token, ttl);
        redisTemplate.opsForValue().set(lookupKey, phoneNumber, ttl);
    }

    public String getPhoneNumberByTempToken(String token) {
        String lookupKey = TEMP_TOKEN_LOOKUP_PREFIX + token;
        return redisTemplate.opsForValue().get(lookupKey);
    }

    public void deleteTempToken(String token) {
        String lookupKey = TEMP_TOKEN_LOOKUP_PREFIX + token;
        String phoneNumber = redisTemplate.opsForValue().get(lookupKey);
        if (phoneNumber != null) {
            redisTemplate.delete(TEMP_TOKEN_PREFIX + phoneNumber);
            redisTemplate.delete(lookupKey);
        }
    }

    @Transactional
    public String createTempTokenForUser(String phoneNumber, boolean isLoginToken) {
        // 다시 로그인 할 때 임시토큰이 있다면 그것을 삭제하고 다시 발급함,
        String existingToken = redisTemplate.opsForValue().get(TEMP_TOKEN_PREFIX + phoneNumber);
        Optional.ofNullable(existingToken).ifPresent(this::deleteTempToken);

        String tempToken = UUID.randomUUID().toString();
        long ttl = isLoginToken ? 3600 : 5 * 60; // 로그인 토큰은 1시간, 회원가입용 토큰은 5분
        setTempToken(phoneNumber, tempToken, ttl);
        return tempToken;
    }
}
