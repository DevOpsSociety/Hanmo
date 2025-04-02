package org.example.hanmo.dto.matching.request;

import org.example.hanmo.domain.MatchingGroupsEntity;
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
    private Gender gender;
    private UserStatus userStatus;
    private MatchingType matchingType;
    private Department department;
    private String name;
    private String phoneNumber;
    private String nickname;
    private String instagramId;
    private String studentNumber;
    private Mbti mbti;
    private MatchingGroupsEntity group;

    public UserEntity toUserEntity() {
        return UserEntity.builder()
                .name(this.getName())
                .phoneNumber(this.getPhoneNumber())
                .nickname(this.getNickname())
                .matchingGroup(this.getGroup())
                .gender(this.getGender())
                .instagramId(this.getInstagramId())
                .studentNumber(this.getStudentNumber())
                .userStatus(this.getUserStatus())
                .department(this.getDepartment())
                .mbti(this.getMbti())
                .matchingType(this.getMatchingType())
                .build();
    }
}
