package org.example.hanmo.dto.matching.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hanmo.domain.enums.Gender;
import org.example.hanmo.domain.enums.UserStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OneToOneMatchingRequest {
    private Long userId;
    private Gender gender;
    private UserStatus userStatus;
}
