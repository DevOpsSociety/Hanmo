package org.example.hanmo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "phone_auth")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsEntity extends BaseTimeEntity { //인증 된 핸드폰 entity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phone_auth_id") // PK
    private Long phoneAuthId;

    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Column(name = "auth_code", length = 10)
    private String authCode;

    private java.time.LocalDateTime expiredAt;

    private Boolean isVerified;
}
