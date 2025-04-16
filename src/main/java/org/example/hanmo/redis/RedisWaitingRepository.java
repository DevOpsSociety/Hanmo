package org.example.hanmo.redis;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.dto.matching.request.RedisUserDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisWaitingRepository {
  private final RedisTemplate<String, RedisUserDto> redisUserTemplate;
  private final RedisTemplate<String, UserEntity> redisTemplate;

  private static final long WAITING_USER_TTL_MINUTES = 180;

  private String getKey(MatchingType matchingType) {
    return matchingType.name();
  }

  // 대기열에 유저 추가
  public void addUserToWaitingGroupInRedis(RedisUserDto userDto, MatchingType matchingType) {
    String key = getKey(matchingType);
    redisUserTemplate.opsForList().rightPush(key, userDto);
    redisUserTemplate.expire(key, WAITING_USER_TTL_MINUTES, TimeUnit.MINUTES);
  }

  // 매칭 타입에 해당하는 대기 유저 목록 조회
  public List<RedisUserDto> getWaitingUsers(MatchingType matchingType) {
    return redisUserTemplate.opsForList().range(getKey(matchingType), 0, -1);
  }

  // 특정 유저를 대기열에서 제거
  public void removeUserFromWaitingGroup(MatchingType matchingType, List<RedisUserDto> users) {
    String key = getKey(matchingType);
    for (RedisUserDto user : users) {
      redisUserTemplate.opsForList().remove(key, 1, user);
    }
  }

  // 필요시 전체 대기열 키 삭제 (특정 매칭 타입의 전체 대기열 삭제)
  public void clearWaitingGroup(MatchingType matchingType) {
    redisUserTemplate.delete(getKey(matchingType));
  }
}
