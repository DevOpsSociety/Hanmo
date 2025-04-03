package org.example.hanmo.dto.matching.request;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseMatchingRequest {
    private Long userId;
    private Long groupId;
    private MatchingType matchingType;
    private Gender gender;
    private Department department;

    //    private MatchingGroupsEntity group;

    public UserEntity toUserEntity() {
        return UserEntity.builder()
                .id(userId)
                //                .matchingGroup(matchingGroup)
//                .matchingType(this.getMatchingType())
                .gender(this.getGender())
                .department(this.getDepartment())
                .build();
    }
}
