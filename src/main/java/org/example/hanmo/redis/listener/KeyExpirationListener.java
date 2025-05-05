package org.example.hanmo.redis.listener;

import java.nio.charset.StandardCharsets;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.GenderMatchingType;
import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.domain.enums.UserStatus;
import org.example.hanmo.redis.RedisWaitingRepository;
import org.example.hanmo.repository.UserRepository;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * 1:1, 2:2 매칭 성공시 그 유저에 대해 키를 발급합니다. 키가 하루동안 유지되며 키가 삭제됨과 동시에 매칭상태, 매칭타입이 null로 변경되어 다시 매칭 시도가
 * 가능합니다. 매칭키는 3시간이며, 매칭 실패시 키 삭제와 동시에 매칭값이 null이 됩니다.
 */
@Slf4j
@Component
public class KeyExpirationListener implements MessageListener {

  private static final String COOLDOWN_1TO1_PREFIX = "match:cooldown:1to1:";
  private static final String COOLDOWN_2TO2_PREFIX = "match:cooldown:2to2:";
  private static final String WAITING_1TO1_PREFIX = "match:waiting:1to1:";
  private static final String WAITING_2TO2_PREFIX = "match:waiting:2to2:";
  private final UserRepository userRepository;
  private final RedisWaitingRepository redisWaitingRepository;

  public KeyExpirationListener(
      UserRepository userRepository, RedisWaitingRepository redisWaitingRepository) {
    this.userRepository = userRepository;
    this.redisWaitingRepository = redisWaitingRepository;
  }

  @Override
  public void onMessage(Message message, byte[] pattern) {
    String expiredKey = new String(message.getBody(), StandardCharsets.UTF_8);
    log.info("[KeyExpired] expiredKey={}", expiredKey);

    if (expiredKey.startsWith(WAITING_1TO1_PREFIX)) {
      rollbackPendingMatching(MatchingType.ONE_TO_ONE, GenderMatchingType.SAME_GENDER);
      rollbackPendingMatching(MatchingType.ONE_TO_ONE, GenderMatchingType.DIFFERENT_GENDER);
      return;
    }
    if (expiredKey.startsWith(WAITING_2TO2_PREFIX)) {
      rollbackPendingMatching(MatchingType.TWO_TO_TWO, GenderMatchingType.DIFFERENT_GENDER);
      return;
    }

    // 1) 기존 ONE_TO_ONE / TWO_TO_TWO 매칭 타임아웃 처리 (원래 로직)
    try {
      MatchingType matchingType = MatchingType.valueOf(expiredKey);

      if (matchingType == MatchingType.ONE_TO_ONE) {
        rollbackPendingMatching(matchingType, GenderMatchingType.SAME_GENDER); // 1:1 동성
        rollbackPendingMatching(matchingType, GenderMatchingType.DIFFERENT_GENDER); // 1:1 이성
      } else if (matchingType == MatchingType.TWO_TO_TWO) {
        rollbackPendingMatching(matchingType, GenderMatchingType.DIFFERENT_GENDER); // 2:2 이성
      }
      return;
    } catch (IllegalArgumentException ignored) {
      // ExpiredKey가 ONE_TO_ONE/TWO_TO_TWO 가 아니면 넘어감
    }

    // 2) 1:1 쿨다운 만료 → userStatus, matchingType 초기화
    if (expiredKey.startsWith(COOLDOWN_1TO1_PREFIX)) {
      Long userId = Long.valueOf(expiredKey.substring(COOLDOWN_1TO1_PREFIX.length()));
      userRepository
          .findById(userId)
          .ifPresent(
              u -> {
                u.setUserStatus(null);
                u.setMatchingType(null);
                u.setGenderMatchingType(null);
                userRepository.save(u);
              });
      return;
    }

    // 3) 2:2 쿨다운 만료 → userStatus, matchingType 초기화
    if (expiredKey.startsWith(COOLDOWN_2TO2_PREFIX)) {
      Long userId = Long.valueOf(expiredKey.substring(COOLDOWN_2TO2_PREFIX.length()));
      userRepository
          .findById(userId)
          .ifPresent(
              u -> {
                u.setUserStatus(null);
                u.setMatchingType(null);
                u.setGenderMatchingType(null);
                userRepository.save(u);
              });
    }
  }

  /** 원래 매칭 PENDING 상태의 유저들을 Redis에서 제거하고 DB의 userStatus, matchingType 을 null로 롤백하는 메소드 */
  private void rollbackPendingMatching(MatchingType matchingType, GenderMatchingType genderMatchingType) {
    List<UserEntity> users =
        userRepository.findAllByUserStatusAndMatchingTypeAndGenderMatchingType(UserStatus.PENDING, matchingType, genderMatchingType);

    for (UserEntity u : users) {
      redisWaitingRepository.removeUserFromWaitingGroup(matchingType, genderMatchingType, List.of(u.toRedisUserDto()));
      u.setUserStatus(null);
      u.setMatchingType(null);
      u.setGenderMatchingType(null);
      userRepository.save(u);
    }
  }
}
