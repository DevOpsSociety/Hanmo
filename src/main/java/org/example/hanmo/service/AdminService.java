package org.example.hanmo.service;

import org.example.hanmo.domain.enums.UserRole;
import org.example.hanmo.dto.admin.date.DashboardSignUpDto;
import org.example.hanmo.dto.admin.date.DashboardGroupDto;
import org.example.hanmo.dto.admin.date.QueueInfoResponseDto;
import org.example.hanmo.dto.admin.request.AdminRequestDto;
import org.example.hanmo.dto.admin.request.ManualMatchRequestDto;
import org.example.hanmo.dto.admin.response.AdminMatchingResponseDto;
import org.example.hanmo.dto.admin.response.AdminUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    String loginAdmin(AdminRequestDto requestDto);

    void addAdminInfo(AdminRequestDto dto);
    Page<AdminUserResponseDto> searchUsersByNickname(String tempToken, String keyword, Pageable pageable);

    void deleteUserByNickname(String tempToken,String nickname);
    DashboardGroupDto getDashboardStats(String tempToken);

    DashboardSignUpDto getTodaySignupStats(String tempToken);

    void changeUserRole(String tempToken, Long userId, UserRole newRole);

    List<QueueInfoResponseDto> getQueueStatuses(String tempToken);

    AdminMatchingResponseDto matchUsersManually(String tempToken, ManualMatchRequestDto request);
}