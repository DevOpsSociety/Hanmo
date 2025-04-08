package org.example.hanmo.redis;

import java.util.List;

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

    public void addUserToWaitingGroupInRedis(RedisUserDto userDto, MatchingType matchingType) {
        redisUserTemplate.opsForList().rightPush(matchingType.name(), userDto);
    }

    public List<RedisUserDto> getWaitingUsers(MatchingType matchingType) {
        return redisUserTemplate.opsForList().range(matchingType.name(), 0, -1);
    }

    public void removeUserFromWaitingGroup(MatchingType matchingType, List<RedisUserDto> users) {
        users.stream()
                .limit(2)
                .forEach(
                        user ->
                                redisUserTemplate
                                        .opsForList()
                                        .remove(matchingType.name(), 1, user));
    }
}
