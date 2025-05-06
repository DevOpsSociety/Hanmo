package org.example.hanmo.service;

import org.example.hanmo.dto.admin.request.AdminRequestDto;
import org.example.hanmo.dto.admin.response.AdminUserResponseDto;

import java.util.List;

public interface AdminService {
    String loginAdmin(AdminRequestDto requestDto);

    void addAdminInfo(AdminRequestDto dto);
    List<AdminUserResponseDto> searchUsersByNickname(String tempToken,String nickname);

    void deleteUserByNickname(String tempToken,String nickname);
}
