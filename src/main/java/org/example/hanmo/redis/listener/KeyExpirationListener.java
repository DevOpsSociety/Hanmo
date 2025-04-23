package org.example.hanmo.redis.listener;

import java.util.List;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.domain.enums.UserStatus;
import org.example.hanmo.redis.RedisWaitingRepository;
import org.example.hanmo.repository.UserRepository;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class KeyExpirationListener implements MessageListener {

  private final UserRepository userRepository;
  private final RedisWaitingRepository redisWaitingRepository;

  public KeyExpirationListener(
      UserRepository userRepository, RedisWaitingRepository redisWaitingRepository) {
    this.userRepository = userRepository;
    this.redisWaitingRepository = redisWaitingRepository;
  }

  @Override
  public void onMessage(Message message, byte[] pattern) {
    // 만료된 키 문자열 (e.g., "ONE_TO_ONE" or "TWO_TO_TWO")
    String expiredKey = message.toString();
    MatchingType type;
    try {
      type = MatchingType.valueOf(expiredKey);
    } catch (IllegalArgumentException e) {
      // 매칭 관련 키가 아니면 즉시 종료
      return;
    }

    List<UserEntity> users =
        userRepository.findAllByUserStatusAndMatchingType(UserStatus.PENDING, type);

    // DB에서 해당 타입의 PENDING 상태 유저 목록 조회
    for (UserEntity u : users) {
      // 각 유저에 대해 Redis 리스트 제거 및 DB 롤백 수행
      redisWaitingRepository.removeUserFromWaitingGroup(type, List.of(u.toRedisUserDto()));
      u.setUserStatus(null);
      u.setMatchingType(null);
      userRepository.save(u);
    }
  }
}
