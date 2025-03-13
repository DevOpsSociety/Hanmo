package org.example.hanmo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mbti")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mbti extends BaseTimeEntity { //고유 mbti 16개 저장

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mbti_id")
    private Long id;

    // 예: ENFP, ISTJ 등
    @Column(name = "type", length = 4, nullable = false, unique = true)
    private String type;
}