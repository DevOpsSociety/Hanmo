package org.example.hanmo.dto.matching.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchingResponse {
    private List<MatchingUserInfo> matchedUsers;

    public void setMatchedUsers(List<MatchingUserInfo> matchingUserInfos) {
    }
}
