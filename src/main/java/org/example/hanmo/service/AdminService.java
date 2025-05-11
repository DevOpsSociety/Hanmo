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
    void resetUserMatchingInfo(Long userId);
    String loginAdmin(AdminRequestDto requestDto);
    void addAdminInfo(AdminRequestDto dto);
    Page<AdminUserResponseDto> searchUsersByNickname(String keyword, Pageable pageable);
    void deleteUserByNickname(String nickname);
    DashboardGroupDto getDashboardStats();
    DashboardSignUpDto getTodaySignupStats();
    void changeUserRole(Long userId, UserRole newRole);
    List<QueueInfoResponseDto> getQueueStatuses();
    AdminMatchingResponseDto matchUsersManually(ManualMatchRequestDto request);
}
