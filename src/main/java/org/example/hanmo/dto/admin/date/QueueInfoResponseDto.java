package org.example.hanmo.dto.admin.date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.hanmo.domain.enums.GenderMatchingType;
import org.example.hanmo.domain.enums.MatchingType;

@Getter
@AllArgsConstructor
public class QueueInfoResponseDto {
    private MatchingType matchingType;
    private GenderMatchingType genderMatchingType;
    private long waitingCount;
}