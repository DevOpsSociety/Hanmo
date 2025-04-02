package org.example.hanmo.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.example.hanmo.domain.enums.*;
import org.example.hanmo.dto.matching.response.MatchingUserInfo;

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

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number", length = 15, nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 1)
    private Gender gender;

    @Column(name = "instagram_id", length = 100)
    private String instagramId;

    @Column(name = "student_number", length = 20, unique = true)
    private String studentNumber;

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
    @Column(name = "matching_type")
    private MatchingType matchingType;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEntity> post = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "matching_group_id")
    private MatchingGroupsEntity matchingGroup;

    public void setMatchingGroup(MatchingGroupsEntity group) {
        this.matchingGroup = group;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    // dto 변환 메서드
    public MatchingUserInfo toMatchingUserInfo() {
        return new MatchingUserInfo(this.getNickname(), this.getInstagramId());
    }
}
