package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.Mbti;
import org.example.hanmo.dto.matching.request.PreferMbtiRequest;
import org.example.hanmo.dto.matching.request.RedisUserDto;
import org.example.hanmo.service.PreferFilterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreferFilterServiceImpl implements PreferFilterService {

  @Override
  public List<RedisUserDto> filterByMbti(String myMbti, PreferMbtiRequest myPrefer, List<RedisUserDto> candidates) {
    List<String> myPreferredMbtiList = expandPreferredMbti(myPrefer);

    return candidates.stream()
        .filter(candidate -> {
          // 내 선호 MBTI 조건 검사 (상대방 MBTI가 내 선호에 포함)
          boolean matchMyPreference = myPreferredMbtiList.isEmpty()
              || myPreferredMbtiList.contains(candidate.getMbti().name());

          // 상대방 선호 MBTI 조건 검사 (내 MBTI가 상대방 선호에 포함)
          List<String> theirPreferredMbtiList = expandPreferredMbti(candidate.getPreferMbtiRequest());
          boolean matchTheirPreference = theirPreferredMbtiList.isEmpty()
              || theirPreferredMbtiList.contains(myMbti);

          return matchMyPreference && matchTheirPreference;
        })
        .collect(Collectors.toList());
  }

  private List<String> expandPreferredMbti(PreferMbtiRequest prefer) {
    if (prefer == null || (prefer.getEiMbti() == null && prefer.getFtMbti() == null)) {
      return List.of(); // 선호 없음 → 모든 MBTI 허용
    }

    return Arrays.stream(Mbti.values())
        .map(Enum::name)
        .filter(mbti -> {
          boolean eiMatch = prefer.getEiMbti() == null || mbti.startsWith(prefer.getEiMbti());
          boolean ftMatch = prefer.getFtMbti() == null || mbti.charAt(2) == prefer.getFtMbti().charAt(0);
          return eiMatch && ftMatch;
        })
        .collect(Collectors.toList());
  }

}
