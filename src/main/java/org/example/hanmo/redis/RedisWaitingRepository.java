package org.example.hanmo.redis;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.UserEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisWaitingRepository {
    private final RedisTemplate<String, UserEntity> redisTemplate;

    // 대기 그룹에 사용자 추가
    public void addUserToWaitingGroup(Long groupId, UserEntity user) {
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
