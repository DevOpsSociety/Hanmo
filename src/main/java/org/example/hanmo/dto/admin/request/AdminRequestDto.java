package org.example.hanmo.dto.admin.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequestDto {
    private String phoneNumber;
    private String loginId;
    private String loginPw;
}
