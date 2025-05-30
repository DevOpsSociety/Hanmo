package org.example.hanmo.dto.matching.response;

import java.util.Collections;
import java.util.List;

import org.example.hanmo.domain.enums.GenderMatchingType;
import org.example.hanmo.domain.enums.MatchingType;

import lombok.Getter;

@Getter
public class MatchingResponse {
  private Long roomId;
  private List<MatchingUserInfo> matchedUsers;
  private MatchingType matchingType;
  private GenderMatchingType genderMatchingType;
  private String code;
  private String message;

  // 전체 필드 생성자 (필요 시 사용)
  public MatchingResponse(Long roomId,
      List<MatchingUserInfo> matchedUsers,
      MatchingType matchingType,
      GenderMatchingType genderMatchingType,
      String code,
      String message) {
    this.roomId = roomId;
    this.matchedUsers = matchedUsers;
    this.matchingType = matchingType;
    this.genderMatchingType = genderMatchingType;
    this.code = code;
    this.message = message;
  }

  // 매칭 성공: roomId 포함
  public MatchingResponse(Long roomId,
      List<MatchingUserInfo> matchedUsers,
      MatchingType matchingType,
      GenderMatchingType genderMatchingType) {
    this.roomId = roomId;
    this.matchedUsers = matchedUsers;
    this.matchingType = matchingType;
    this.genderMatchingType = genderMatchingType;
    this.code = "MATCHED";
    this.message = "매칭이 완료되었습니다.";
  }

  // 매칭 대기 상태: roomId 없음
  public MatchingResponse(MatchingType matchingType,
      GenderMatchingType genderMatchingType) {
    this.roomId = null;
    this.matchedUsers = Collections.emptyList();
    this.matchingType = matchingType;
    this.genderMatchingType = genderMatchingType;
    this.code = "WAITING_FOR_MATCHING";
    this.message = "매칭 대기 등록이 완료되었습니다.";
  }
}
