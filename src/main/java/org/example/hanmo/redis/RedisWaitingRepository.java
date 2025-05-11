package org.example.hanmo.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.GenderMatchingType;
import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.domain.enums.UserStatus;
import org.example.hanmo.dto.admin.date.QueueInfoResponseDto;
import org.example.hanmo.dto.matching.request.RedisUserDto;
import org.example.hanmo.repository.user.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisWaitingRepository {
  private final RedisTemplate<String, RedisUserDto> redisUserTemplate;
  private final UserRepository userRepository;

  private static final long WAITING_USER_TTL_MINUTES = 180;

  private String getKey(MatchingType matchingType, GenderMatchingType genderMatchingType) {
    return matchingType.name() + "_" + genderMatchingType.name();
  }

  // 대기열에 유저 추가
  public void addUserToWaitingGroupInRedis(RedisUserDto userDto, MatchingType matchingType, GenderMatchingType genderMatchingType) {
    String key = getKey(matchingType, genderMatchingType);
    redisUserTemplate.opsForList().remove(key, 0, userDto);
    redisUserTemplate.opsForList().rightPush(key, userDto);
    redisUserTemplate.expire(key, WAITING_USER_TTL_MINUTES, TimeUnit.MINUTES);
  }

  // 매칭 타입, 매칭 성별 타입에 해당하는 대기 유저 목록 조회
  public List<RedisUserDto> getWaitingUsers(MatchingType matchingType, GenderMatchingType genderMatchingType) {
    return redisUserTemplate.opsForList().range(getKey(matchingType, genderMatchingType), 0, -1);
  }

  // 특정 유저를 대기열에서 제거
  public void removeUserFromWaitingGroup(MatchingType matchingType, GenderMatchingType genderMatchingType, List<RedisUserDto> users) {
    String key = getKey(matchingType, genderMatchingType);
    for (RedisUserDto user : users) {
      redisUserTemplate.opsForList().remove(key, 1, user);
    }
  }

  // 필요시 전체 대기열 키 삭제 (특정 매칭 타입의 전체 대기열 삭제)
  // 웨이팅 3시간으로 REDIS키값이 지워지면 DB에도 이를 감지하고 트랙잭션으로 감지한 후, DB롤백
  public void clearWaitingGroup(MatchingType matchingType, GenderMatchingType genderMatchingType) {

    // 1) 키 삭제 전에 DB 롤백
    List<UserEntity> waiting = userRepository.findAllByUserStatusAndMatchingTypeAndGenderMatchingType(UserStatus.PENDING, matchingType, genderMatchingType);
    waiting.forEach(u -> {
      u.setUserStatus(null);
      u.setMatchingType(null);
      u.setGenderMatchingType(null);
    });
    userRepository.saveAll(waiting);

    redisUserTemplate.delete(getKey(matchingType, genderMatchingType));
  }

  public List<QueueInfoResponseDto> getQueueStatuses() {
    List<QueueInfoResponseDto> list = new ArrayList<>();

    for (MatchingType mt : MatchingType.values()) {
      for (GenderMatchingType gmt : GenderMatchingType.values()) {
        String key = getKey(mt, gmt);
        Long size = redisUserTemplate.opsForList().size(key);
        list.add(new QueueInfoResponseDto(mt, gmt, size == null ? 0L : size));
      }
    }
    return list;
  }

  //위에 삭제키가 있지만, 레디스 dto를 받아와 생성자를 다시 만들어야하는 번거로움이 있어 추가합니다. - 축제 이후 삭제예정
  public void removeUserById(Long userId) {
    for (MatchingType mt : MatchingType.values()) {
      for (GenderMatchingType gt : GenderMatchingType.values()) {
        String key = getKey(mt, gt);
        List<RedisUserDto> list = redisUserTemplate.opsForList().range(key, 0, -1);
        if (list == null) continue;

        list.stream()
                .filter(dto -> dto.getId().equals(userId))
                .findFirst()
                .ifPresent(dto -> redisUserTemplate.opsForList().remove(key, 1, dto));
      }
    }
  }
}