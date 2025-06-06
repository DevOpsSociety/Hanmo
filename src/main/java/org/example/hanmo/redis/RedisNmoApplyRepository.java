package org.example.hanmo.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisNmoApplyRepository {

  private final StringRedisTemplate redisTemplate;

  // 키 생성 규칙
  private String getApplyCountKey(Long nmoId) {
    return "nmo:" + nmoId + ":applyCount";
  }

  //해당 Nmo 신청자 수 1 증가 (신청 시 호출)
  public void incrementApplyCount(Long nmoId) {
    String key = getApplyCountKey(nmoId);
    redisTemplate.opsForValue().increment(key);
  }

  // 해당 Nmo 신청자 수 1 감소 (취소 시 호출)
  // 음수가 되는 것 방지 위해 0 미만은 0으로 고정
  public void decrementApplyCount(Long nmoId) {
    String key = getApplyCountKey(nmoId);
    Long count = redisTemplate.opsForValue().decrement(key);
    if (count != null && count < 0) {
      redisTemplate.opsForValue().set(key, "0");
    }
  }

  // 현재 Nmo 신청자 수 조회
  // 키가 없으면 0 반환
  public int getApplyCount(Long nmoId) {
    String countStr = redisTemplate.opsForValue().get(getApplyCountKey(nmoId));
    if (countStr == null) {
      return 0;
    }
    return Integer.parseInt(countStr);
  }

  public void deleteApplyCountKey(Long nmoId) {
    String key = getApplyCountKey(nmoId);
    redisTemplate.delete(key);
  }

  // Nmo 생성 시 신청자 수 0으로 초기화 (필요 시)
  public void initializeApplyCount(Long nmoId) {
    redisTemplate.opsForValue().set(getApplyCountKey(nmoId), "0");
  }
}

