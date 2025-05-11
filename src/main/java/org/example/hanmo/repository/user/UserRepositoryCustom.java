package org.example.hanmo.repository.user;

import org.example.hanmo.domain.enums.UserStatus;
import org.example.hanmo.dto.admin.response.AdminUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserRepositoryCustom {

    Page<AdminUserResponseDto> searchUsersByKeyword(String keyword, UserStatus userStatus, Pageable pageable);
}
