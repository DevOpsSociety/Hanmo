package org.example.hanmo.repository.user;

import org.example.hanmo.dto.admin.response.AdminUserResponseDto;
import java.util.List;

public interface UserRepositoryCustom {

    List<AdminUserResponseDto> searchUsersByNickname(String nickname, int page);
}
