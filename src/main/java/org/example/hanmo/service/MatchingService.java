package org.example.hanmo.service;

import java.util.List;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.matching.request.RedisUserDto;
import org.example.hanmo.dto.matching.response.MatchingResponse;
import org.example.hanmo.dto.matching.response.MatchingResultResponse;
import org.springframework.stereotype.Service;

@Service
public interface MatchingService {
  // 1:1 동성 매칭 대기
  void waitingSameGenderOneToOneMatching(RedisUserDto userDto);

  // 1:1 이성 매칭 대기
  void waitingDifferentGenderOneToOneMatching(RedisUserDto userDto);

  // 2:2 매칭 대기
  void waitingTwoToTwoMatching(RedisUserDto userDto);

  // 1:1 동성 매칭 그룹 생성
  MatchingResponse createSameGenderOneToOneMatchingGroup(List<UserEntity> users);

  // 1:1 이성 매칭 그룹 생성
  MatchingResponse createDifferentGenderOneToOneMatchingGroup(List<UserEntity> users);

  // 2:2 매칭 그룹 생성
  MatchingResponse createTwoToTwoMatchingGroup(List<UserEntity> users);

  // 1:1 동성 매칭
  MatchingResponse matchSameGenderOneToOne(String tempToken);

  // 1:1 이성 매칭
  MatchingResponse matchDifferentGenderOneToOne(String tempToken);

  // 2:2 매칭
  MatchingResponse matchDifferentGenderTwoToTwo(String tempToken);

  // 매칭 결과 조회
  MatchingResultResponse getMatchingResult(String tempToken);

  // 매칭 취소
  void cancelMatching(String tempToken);

  void cleanupAfterUserDeletion(String nickname);
}
