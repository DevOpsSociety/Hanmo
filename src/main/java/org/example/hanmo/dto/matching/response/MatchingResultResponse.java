package org.example.hanmo.dto.matching.response;

import java.util.List;

import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.dto.user.response.UserProfileResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MatchingResultResponse {
  private Long matchingGroupId;
  private MatchingType matchingType;
  private List<UserProfileResponseDto> users;
}
