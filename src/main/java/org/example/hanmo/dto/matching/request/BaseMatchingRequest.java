package org.example.hanmo.dto.matching.request;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.Department;
import org.example.hanmo.domain.enums.Gender;
import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.domain.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseMatchingRequest {
    private Long userId;
    private Gender gender;
    private UserStatus userStatus;
    private MatchingType matchingType;
    private Department department;

    public UserEntity toUserEntity() {
        return UserEntity.builder()
                //                .name(this.getName())
                //                .phoneNumber(this.getPhoneNumber())
                //                .nickname(this.getNickname())
                .gender(this.getGender())
                //                .instagramId(this.getInstagramId())
                //                .studentNumber(this.getStudentNumber())
                .userStatus(this.getUserStatus())
                .department(this.getDepartment())
                //                .mbti(this.getMbti())
                .matchingType(this.getMatchingType())
                .build();
    }
}
