package org.example.hanmo.redis;

import java.util.List;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.MatchingType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisWaitingRepository {
    private final RedisTemplate<String, UserEntity> redisTemplate;

    public RedisWaitingRepository(RedisTemplate<String, UserEntity> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 대기 그룹에 사용자 추가
    public void addUserToWaitingGroupInRedis(Long groupId, UserEntity user, MatchingType matchingType) {
        redisTemplate.opsForList().rightPush(String.valueOf(groupId), user);
    }

    // 대기 그룹에서 사용자 가져오기
    public List<UserEntity> getWaitingUser(Long groupId) {
        return redisTemplate.opsForList().range(String.valueOf(groupId), 0, -1);
    }

    // 대기 그룹에 사용자 제거
    public void removeUserFromWaitingGroup(Long groupId, UserEntity user) {
        redisTemplate.opsForList().remove(String.valueOf(groupId), 1, user);
    }
}
