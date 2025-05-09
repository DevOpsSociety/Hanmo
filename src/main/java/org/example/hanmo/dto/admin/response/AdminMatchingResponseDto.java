package org.example.hanmo.dto.admin.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hanmo.domain.enums.GenderMatchingType;
import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.dto.matching.response.MatchingResponse;
import org.example.hanmo.dto.matching.response.MatchingUserInfo;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdminMatchingResponseDto {
    private MatchingType matchingType;
    private GenderMatchingType genderMatchingType;
    private List<MatchingUserInfo> matchedUsers;

    public AdminMatchingResponseDto(MatchingResponse response) {
        this.matchingType = response.getMatchingType();
        this.genderMatchingType = response.getGenderMatchingType();
        this.matchedUsers = response.getMatchedUsers();
    }
}