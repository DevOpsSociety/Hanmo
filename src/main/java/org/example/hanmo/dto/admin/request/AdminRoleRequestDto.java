package org.example.hanmo.dto.admin.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hanmo.domain.enums.UserRole;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminRoleRequestDto {
    private Long userId;
    private UserRole newRole;

}
