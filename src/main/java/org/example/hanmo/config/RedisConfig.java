package org.example.hanmo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {
    private final String redisHost; // Redis 서버 호스트
    private final int redisPort; // Redis 서버 포트

    public RedisConfig(@Value("${spring.data.redis.host}") final String redisHost,
                       @Value("${spring.data.redis.port}") final int redisPort) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer()); // Key는 문자열로 직렬화
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // Value는 JSON으로 직렬화
        redisTemplate.setHashKeySerializer(new StringRedisSerializer()); // HashKey는 문자열로 직렬화
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer()); // HashValue는 JSON으로 직렬화
        return redisTemplate;
    }
}
