package org.example.hanmo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.hanmo.domain.enums.PreferredGender;

@Entity
@Table(name = "matching_preferences")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingPreferencesEntity extends BaseTimeEntity { // 이 entity는 선호 상대를 고르는 entity, 추후 확장하기 위해 일단 추가

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_preferences_id")
    private Long matchingPreferencesId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_gender", length = 5)
    private PreferredGender preferredGender;

    @Column(name = "preferred_age_min")
    private Integer preferredAgeMin;

    @Column(name = "preferred_age_max")
    private Integer preferredAgeMax;
}
