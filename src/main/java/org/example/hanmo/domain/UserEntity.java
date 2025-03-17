package org.example.hanmo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hanmo.domain.enums.Department;
import org.example.hanmo.domain.enums.Gender;
import org.example.hanmo.domain.enums.Mbti;
import org.example.hanmo.domain.enums.UserStatus;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseTimeEntity{ //user의 기본 정보
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number", length = 15, nullable = false,unique = true)
    private String phoneNumber;

    @Column(name = "nickname", length = 15)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 1)
    private Gender gender;

    @Column(name = "instagram_id", length = 100)
    private String instagramId;

    @Column(name = "student_number", length = 20, unique = true)
    private String studentNumber;

    @Column(name = "status", length = 20)
    private UserStatus userStatus;   // 대기중, 매칭완료, 탈퇴 그룹의 status와는 다름

    @Enumerated(EnumType.STRING)
    @Column(name = "department")
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "mbti")
    private Mbti mbti;

    @ManyToOne
    @JoinColumn(name = "matching_group_id")
    private MatchingGroupsEntity matchingGroup;
    public void setMatchingGroup(MatchingGroupsEntity group) {
        this.matchingGroup = group;
    }
}
