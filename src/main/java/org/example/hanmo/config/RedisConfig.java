package org.example.hanmo.config;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.matching.request.RedisUserDto;
import org.example.hanmo.redis.listener.KeyExpirationListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {
  private final String redisHost;
  private final int redisPort;
  private final String redisPassword;

  public RedisConfig(
      @Value("${spring.data.redis.host}") final String redisHost,
      @Value("${spring.data.redis.port}") final int redisPort,
      @Value("${spring.data.redis.password}") final String redisPassword) {
    this.redisHost = redisHost;
    this.redisPort = redisPort;
    this.redisPassword = redisPassword;
  }

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
    config.setPassword(RedisPassword.of(redisPassword));
    return new LettuceConnectionFactory(config);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);

    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    return redisTemplate;
  }

  @Bean
  public RedisTemplate<String, UserEntity> UserTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, UserEntity> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
  }

  @Bean
  public RedisTemplate<String, RedisUserDto> redisUserTemplate(
      RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, RedisUserDto> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
  }

  @Bean
  public ApplicationRunner enableKeyspaceNotifications(RedisConnectionFactory factory) {
    return args -> factory.getConnection().setConfig("notify-keyspace-events", "Ex");
  }

  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory connectionFactory, KeyExpirationListener keyExpirationListener) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    // DB 0번의 expired 이벤트만 구독
    container.addMessageListener(keyExpirationListener, new PatternTopic("__keyevent@0__:expired"));
    return container;
  }
}
