package org.example.hanmo.dto.matching.response;

import java.util.List;

import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.domain.enums.UserStatus;
import org.example.hanmo.dto.user.response.UserProfileResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MatchingResultResponse {
  private UserStatus userStatus;
  private MatchingType matchingType;
  private List<UserProfileResponseDto> users;
}
