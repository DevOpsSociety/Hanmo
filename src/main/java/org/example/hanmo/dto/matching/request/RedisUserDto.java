package org.example.hanmo.dto.matching.request;

import java.util.List;
import java.util.stream.Collectors;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.*;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RedisUserDto {
  @EqualsAndHashCode.Include private Long id;
  private String name;
  private String nickname;
  private String instagramId;
  private UserStatus userStatus;
  private Department department;
  private Mbti mbti;
  private MatchingType matchingType;
  private GenderMatchingType genderMatchingType;
  private Gender gender;
  private Long matchingGroupId;
  private PreferMbtiRequest preferMbtiRequest;
  private Integer studentYear; // 자신의 학번 연도
  private Integer preferredStudentYear; // 선호 학번 연도


  public UserEntity toUserEntity() {
    return UserEntity.builder()
        .id(id)
        .name(name)
        .gender(gender)
        .department(department)
        .userStatus(userStatus)
        .nickname(nickname)
        .instagramId(instagramId)
        .build();
  }

  public static List<UserEntity> toUserEntityList(List<RedisUserDto> userDtoList) {
    return userDtoList.stream().map(RedisUserDto::toUserEntity).collect(Collectors.toList());
  }

  public void setUserStatus(UserStatus userStatus) {
    this.userStatus = userStatus;
  }
  public void setPreferenceMbtiRequest(PreferMbtiRequest preferMbtiRequest) {
    this.preferMbtiRequest = preferMbtiRequest;
  }

  public void setStudentYear(Integer studentYear) {
    this.studentYear = studentYear;
  }

  public void setPreferredStudentYear(Integer preferredStudentYear) {
    this.preferredStudentYear = preferredStudentYear;
  }
}
