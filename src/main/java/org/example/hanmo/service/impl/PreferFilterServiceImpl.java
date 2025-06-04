package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.enums.Mbti;
import org.example.hanmo.dto.matching.request.PreferMbtiRequest;
import org.example.hanmo.dto.matching.request.RedisUserDto;
import org.example.hanmo.service.PreferFilterService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreferFilterServiceImpl implements PreferFilterService {

  // MBTI선호 필터
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


  // 선호 MBTI 문자열 리스트 추출 기능
  // null :  E,I or F,T : E,I and F,T 이렇게 총 4가지 방식
  // null 이면 mbti 상관없이 모든 MBTI 반환
  // 한개라도 있으면 선호 mbti를 한개라도 가지고 있는 MBTI 반환
  // 둘다 있으면 선호 MBTI를 둘다 가지고 있는 MBTI 반환
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

    @Override
    public List<RedisUserDto> filterByStudentYear(Integer myPreferredStudentYear, List<RedisUserDto> candidates) {
        if (myPreferredStudentYear == null || myPreferredStudentYear < 2018) {
            return candidates; // 선호 학번이 없거나 18보다 위인 경우, 모든 후보 허용
        }

        return candidates.stream()
                .filter(candidate -> {
                    Integer candidateStudentYear = candidate.getStudentYear();
                    if (candidateStudentYear == null) {
                        return false; // 학번 정보 없으면 후보에서 제외 (혹시 데이터 누락이 발생한다면 그냥 후보에서 제외함)
                    }

                    return (candidateStudentYear >= myPreferredStudentYear -1) && (candidateStudentYear <= myPreferredStudentYear + 1);
                })
                .collect(Collectors.toList());

    }

    public List<RedisUserDto> filterByMutualStudentYear(Integer myPreferredStudentYear, Integer myStudentYear, List<RedisUserDto> candidates) {
        // 본인 기준 학번 필터링
        List<RedisUserDto> filteredByMyYear = filterByStudentYear(myPreferredStudentYear, candidates);

        // 상대방 기준 학번 필터링
        return filteredByMyYear.stream()
                .filter(candidate -> {
                    Integer theirPreferredYear = candidate.getPreferredStudentYear();

                    // 상대방이 선호 학번 지정 안 했을 경우, 그대로 통과
                    if (theirPreferredYear == null) {
                        return true;
                    }

                    return myStudentYear != null &&
                            myStudentYear >= theirPreferredYear - 1 &&
                            myStudentYear <= theirPreferredYear + 1;
                })
                .collect(Collectors.toList());

    }

}
