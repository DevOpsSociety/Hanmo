package org.example.hanmo.service;

import org.example.hanmo.dto.matching.request.PreferMbtiRequest;
import org.example.hanmo.dto.matching.request.RedisUserDto;

import java.util.List;

public interface PreferFilterService {
  // 선호 mbti이 필터링
  List<RedisUserDto> filterByMbti(String myMbti, PreferMbtiRequest myPrefer, List<RedisUserDto> candidates);
}
