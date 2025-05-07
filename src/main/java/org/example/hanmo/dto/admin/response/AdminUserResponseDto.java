package org.example.hanmo.dto.admin.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.domain.enums.UserStatus;

@Getter
@AllArgsConstructor
public class AdminUserResponseDto {
    private Long userId;
    private String nickname;
    private String name;
    private String phoneNumber;
    private String instagramId;
    private String userRole;
    private UserStatus userStatus;
    private Long matchingGroupId;
    private MatchingType matchingType;
}
