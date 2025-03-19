package org.example.hanmo.redis;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

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
    public String createTempTokenForUser(String phoneNumber) {
        String tempToken = UUID.randomUUID().toString();
        setTempToken(phoneNumber, tempToken, 5 * 60);
        return tempToken;
    }
}