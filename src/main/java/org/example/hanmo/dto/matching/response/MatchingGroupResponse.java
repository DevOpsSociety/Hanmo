package org.example.hanmo.dto.matching.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchingGroupResponse {
    private Long groupId;
    private Integer maleCount;
    private Integer femaleCount;
    private String groupStatus;
}
