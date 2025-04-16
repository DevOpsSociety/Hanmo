package org.example.hanmo.controller.test;

import java.util.List;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.dto.matching.request.RedisUserDto;
import org.example.hanmo.redis.RedisWaitingRepository;
import org.example.hanmo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestDataController {

  private final UserRepository userRepository;
  private final RedisWaitingRepository redisWaitingRepository;

  @Operation(summary = "Redis Test User", description = "DB에 있는 Test User를 Redis에 추가합니다.")
  @PostMapping("/redis-init")
  public String initializeRedisFromDb() {
    for (MatchingType type : MatchingType.values()) {
      // 해당 MatchingType을 가진 유저들 DB에서 가져오기
      List<UserEntity> users = userRepository.findAllByMatchingType(type);

      // RedisUserDto로 변환
      List<RedisUserDto> redisUserDtoList =
          users.stream()
              .map(
                  user ->
                      RedisUserDto.builder()
                          .id(user.getId())
                          .name(user.getName())
                          .gender(user.getGender())
                          .department(user.getDepartment())
                          .userStatus(user.getUserStatus())
                          .nickname(user.getNickname())
                          .nicknameChanged(user.getNicknameChanged())
                          .instagramId(user.getInstagramId())
                          .phoneNumber(user.getPhoneNumber())
                          .studentNumber(user.getStudentNumber())
                          .mbti(user.getMbti())
                          .matchingType(user.getMatchingType())
                          .build())
              .toList();

      // Redis에 저장
      redisUserDtoList.forEach(
          userDto -> redisWaitingRepository.addUserToWaitingGroupInRedis(userDto, type));
    }

    return "✅ Redis 초기화 완료 (DB → Redis)";
  }
}
