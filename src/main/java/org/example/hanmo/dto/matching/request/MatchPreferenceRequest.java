package org.example.hanmo.dto.matching.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class MatchPreferenceRequest {
    @Schema(description = "MBTI E or I")
    String eiMbti;

    @Schema(description = "MBTI F or T")
    String ftMbti;

    @Schema(description = "선호 학번(입학 연도 4자리)")
    Integer preferredStudentYear;
}
