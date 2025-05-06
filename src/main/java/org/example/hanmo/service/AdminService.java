package org.example.hanmo.service;

import org.example.hanmo.dto.admin.request.AdminRequestDto;

public interface AdminService {
    String loginAdmin(AdminRequestDto requestDto);

    void addAdminInfo(AdminRequestDto dto);
}
