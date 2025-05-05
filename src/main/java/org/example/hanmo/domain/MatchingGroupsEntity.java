package org.example.hanmo.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.example.hanmo.domain.enums.GenderMatchingType;
import org.example.hanmo.domain.enums.GroupStatus;
import org.example.hanmo.domain.enums.MatchingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "matching_groups")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingGroupsEntity extends BaseTimeEntity { // 매칭 시작 시 한명씩 들어감, 4명 되면 매칭 완료 상태로 변경
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "matching_group_id")
  private Long matchingGroupId;

  private Integer maleCount;

  private Integer femaleCount;

  private Boolean isSameDepartment;

  @Enumerated(EnumType.STRING)
  @Column(name = "group_status", length = 20)
  private GroupStatus groupStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "matching_type", length = 20)
  private MatchingType matchingType;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender_matching_type", length = 20)
  private GenderMatchingType genderMatchingType;

  @OneToMany(mappedBy = "matchingGroup", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @Builder.Default
  private List<UserEntity> users = new ArrayList<>();

  public void addUser(UserEntity user) {
    users.add(user);
    user.setMatchingGroup(this);
  }

  public void setMatchingType(MatchingType matchingType) {
    this.matchingType = matchingType;
  }
}
