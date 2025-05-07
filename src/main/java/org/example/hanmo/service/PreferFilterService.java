package org.example.hanmo.service;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.Gender;
import org.example.hanmo.domain.enums.Mbti;
import org.example.hanmo.dto.matching.request.PreferMbtiRequest;
import org.example.hanmo.dto.matching.request.RedisUserDto;

import java.util.List;

public interface PreferFilterService {
  // 선호 mbti이 필터링
  List<RedisUserDto> filterByMbti(Gender myGender, String myMbti, PreferMbtiRequest myPrefer, List<RedisUserDto> candidates);
}
