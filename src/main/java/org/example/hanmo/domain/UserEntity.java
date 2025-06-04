package org.example.hanmo.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.example.hanmo.domain.enums.*;
import org.example.hanmo.dto.matching.request.RedisUserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseTimeEntity { // user의 기본 정보
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "phone_number", length = 15, nullable = false, unique = true)
  private String phoneNumber;

  @Column(name = "nickname", length = 50)
  private String nickname;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", length = 1)
  private Gender gender;

  @Column(name = "instagram_id", length = 100, nullable = false)
  private String instagramId;

  @Column(name = "student_number", length = 20, unique = true, nullable = false)
  private String studentNumber;

  @Column(name = "login_id", length = 30, unique = true)
  private String loginId;

  @Column(name = "login_pw", length = 100)
  private String loginPw;

  @Column(name = "nickname_changed", nullable = false)
  private Boolean nicknameChanged = false; // 기본값 false 닉네임 1회변경 한번 바꾸면 true로

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private UserStatus userStatus; // 매칭 대기, 매칭 완료, 탈퇴 (그룹의 status와는 다름)

  @Enumerated(EnumType.STRING)
  @Column(name = "department")
  private Department department;

  @Enumerated(EnumType.STRING)
  @Column(name = "mbti")
  private Mbti mbti;

  @Enumerated(EnumType.STRING)
  @Column(name = "withdrawal_status", nullable = false)
  @Builder.Default
  private WithdrawalStatus withdrawalStatus = WithdrawalStatus.ACTIVE;

  // 탈퇴(휴면) 시각 (복구 가능 기간 체크용)
  @Column(name = "withdrawal_timestamp")
  private LocalDateTime withdrawalTimestamp;

  @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostEntity> post = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  @Column(name = "matching_type")
  private MatchingType matchingType;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender_matching_type", length = 20)
  private GenderMatchingType genderMatchingType;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "matching_group_id")
  private MatchingGroupsEntity matchingGroup;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_role", nullable = false, columnDefinition = "VARCHAR(20) NOT NULL DEFAULT 'USER'"
  )
  private UserRole userRole;

  public void setMatchingGroup(MatchingGroupsEntity group) {
    this.matchingGroup = group;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public boolean isNicknameChanged() {
    return nicknameChanged != null ? nicknameChanged : false;
  }

  public void setNicknameChanged(boolean nicknameChanged) {
    this.nicknameChanged = nicknameChanged;
  }

  public void setUserStatus(UserStatus userStatus) {
    this.userStatus = userStatus;
  }

  public void setMatchingType(MatchingType matchingType) {
    this.matchingType = matchingType;
  }

  public void setGenderMatchingType(GenderMatchingType genderMatchingType) {this.genderMatchingType = genderMatchingType;}

  public void setUserRole(UserRole userRole) {this.userRole = userRole;}

  public void setLoginId(String loginId) {this.loginId = loginId;}
  public void setLoginPw(String loginPw) {this.loginPw = loginPw;}

  // 탈퇴 된 계정을 휴먼상태로 전환해서 시각을 기록함 (하루가 좋을거라고 생각해서 하루로 설정)
  public void deactivateAccount() {
    this.withdrawalStatus = WithdrawalStatus.WITHDRAWN;
    this.withdrawalTimestamp = LocalDateTime.now();
  }

  // 계정 복구
  public void restoreAccount() {
    this.withdrawalStatus = WithdrawalStatus.ACTIVE;
    this.withdrawalTimestamp = null;
  }

  public RedisUserDto toRedisUserDto() {
    // 학번 연도 추출
    Integer studentYear = null;
    if (this.studentNumber != null && this.studentNumber.length() >= 4) {
        try {
            studentYear = Integer.parseInt(this.studentNumber.substring(0, 4));
        } catch (NumberFormatException e) {
            // 학번이 잘못된 형식인 경우 예외 처리
            studentYear = null;
        }
    }

    return RedisUserDto.builder()
        .id(id)
        .name(name)
        .gender(gender)
        .mbti(mbti)
        .department(department)
        .userStatus(userStatus)
        .studentYear(studentYear)
        .build();
  }
}
