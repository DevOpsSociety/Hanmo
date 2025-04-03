package org.example.hanmo.dto.matching.response;

import java.util.List;

import org.example.hanmo.domain.enums.MatchingType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchingResponse {
    private List<MatchingUserInfo> matchedUsers;
    private MatchingType matchingType;
}
