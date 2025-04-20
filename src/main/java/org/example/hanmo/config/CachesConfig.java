package org.example.hanmo.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class CachesConfig {
  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory cf) {
    GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
    RedisSerializationContext.SerializationPair<Object> jsonPair =
        RedisSerializationContext.SerializationPair.fromSerializer(serializer);

    // 캐시별 설정
    Map<String, RedisCacheConfiguration> configs = new HashMap<>();
    configs.put(
        "matchingResult",
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(7))
            .disableCachingNullValues()
            .serializeValuesWith(jsonPair) // JSON 직렬화 적용
        );
    return RedisCacheManager.builder(cf).withInitialCacheConfigurations(configs).build();
  }
}
