package org.example.hanmo.service;

import org.example.hanmo.dto.user.request.AdminRequestDto;

public interface AdminService {
    String loginAdmin(AdminRequestDto requestDto);

    void addAdminInfo(AdminRequestDto dto);
}
