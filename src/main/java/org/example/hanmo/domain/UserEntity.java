package org.example.hanmo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hanmo.domain.enums.Gender;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class UserEntity extends BaseTimeEntity{ //user의 기본 정보
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "phone_number", length = 15, nullable = false,unique = true)
    private String phoneNumber;

    @Column(name = "nickname", length = 15)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 1)
    private Gender gender;

    @Column(name = "age", nullable = false)
    private Long age;

    @Column(name = "instagram_id", length = 100)
    private String instagramId;


    @Column(name = "serial_code", length = 20, unique = true)
    private String serialCode;

    @Column(name = "status", length = 20)
    private String userStatus;   // 대기중, 매칭완료, 탈퇴 그룹의 status와는 다름

    @ManyToOne
    @JoinColumn(name = "matching_group_id")
    private MatchingGroupsEntity matchingGroup;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentEntity department;

    @ManyToOne
    @JoinColumn(name = "mbti_id")
    private MbtiEntity mbti;

    public void setMatchingGroup(MatchingGroupsEntity group) {
        this.matchingGroup = group;
    }
}
