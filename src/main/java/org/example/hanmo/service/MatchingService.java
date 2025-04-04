package org.example.hanmo.service;

import java.util.List;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.matching.request.RedisUserDto;
import org.example.hanmo.dto.matching.response.MatchingResponse;
import org.example.hanmo.dto.user.response.UserProfileResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface MatchingService {

    // 매칭 대기
    void waitingOneToOneMatching(RedisUserDto userDto);

    void waitingTwoToTwoMatching(RedisUserDto userDto);

    // List<UserEntity> filterUsersByGender(List<UserEntity> users, Gender gender);

    MatchingResponse createOneToOneMatchingGroup(List<UserEntity> users);

    MatchingResponse createTwoToTwoMatchingGroup(List<UserEntity> users);

    // 1:1 매칭
    MatchingResponse matchSameGenderOneToOne();

    // 2:2 매칭
    MatchingResponse matchOppositeGenderTwoToTwo();

    // 매칭 결과 조회
    List<UserProfileResponseDto> getMatchingResult(String tempToken);
}
