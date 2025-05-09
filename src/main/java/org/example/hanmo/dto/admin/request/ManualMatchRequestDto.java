package org.example.hanmo.dto.admin.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hanmo.domain.enums.GenderMatchingType;
import org.example.hanmo.domain.enums.MatchingType;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ManualMatchRequestDto {
    private MatchingType matchingType;
    private GenderMatchingType genderMatchingType;
    private List<String> userIds;
}
