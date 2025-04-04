package org.example.hanmo.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

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
public class MatchingGroupsEntity extends BaseTimeEntity { // 매칭 시작시 한명씩 들어감 4명되면 매칭완료 상태로 변경함
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_group_id")
    private Long matchingGroupId;

    private Integer maleCount;

    private Integer femaleCount;

    private Boolean isSameDepartment;

    @Column(length = 20)
    private String groupStatus; // 매칭중, 매칭완료, 취소 //

    @OneToMany(mappedBy = "matchingGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEntity> users = new ArrayList<>();

    public void addUser(UserEntity user) {
        users.add(user);
        user.setMatchingGroup(this);
    }
}
