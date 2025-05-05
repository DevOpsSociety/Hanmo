package org.example.hanmo.dto.matching.response;

import java.util.Collections;
import java.util.List;

import org.example.hanmo.domain.enums.GenderMatchingType;
import org.example.hanmo.domain.enums.MatchingType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingResponse {
  private List<MatchingUserInfo> matchedUsers;
  private MatchingType matchingType;
  private GenderMatchingType genderMatchingType;
  private String code;
  private String message;

  // 매칭 성공
  public MatchingResponse(List<MatchingUserInfo> matchedUsers, MatchingType matchingType, GenderMatchingType genderMatchingType) {
    this.matchedUsers = matchedUsers;
    this.matchingType = matchingType;
    this.genderMatchingType = genderMatchingType;
    this.code = "MATCHED";
    this.message = "매칭이 완료되었습니다.";
  }

  // 매칭 대기 상태
  public MatchingResponse(MatchingType matchingType, GenderMatchingType genderMatchingType) {
    this.matchedUsers = Collections.emptyList();
    this.matchingType = matchingType;
    this.genderMatchingType = genderMatchingType;
    this.code = "WAITING_FOR_MATCHING";
    this.message = "매칭 대기 등록이 완료되었습니다.";
  }
}
