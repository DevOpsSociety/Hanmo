package org.example.hanmo.dto.admin.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminUserResponseDto {
    private Long userId;
    private String nickname;
    private String name;
    private String phoneNumber;
    private String instagramId;
    private String userRole;
}
