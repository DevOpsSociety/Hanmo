package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.aop.AdminCheck;
import org.example.hanmo.domain.enums.GroupStatus;
import org.example.hanmo.domain.enums.UserStatus;
import org.example.hanmo.dto.admin.date.DashboardSignUpDto;
import org.example.hanmo.dto.admin.date.DashboardGroupDto;
import org.example.hanmo.dto.admin.date.QueueInfoResponseDto;
import org.example.hanmo.dto.admin.request.AdminRequestDto;
import org.example.hanmo.dto.admin.request.AdminRoleRequestDto;
import org.example.hanmo.dto.admin.request.ManualMatchRequestDto;
import org.example.hanmo.dto.admin.response.AdminMatchingResponseDto;
import org.example.hanmo.dto.admin.response.AdminUserResponseDto;
import org.example.hanmo.dto.admin.response.PageResponseDto;
import org.example.hanmo.repository.MatchingGroupRepository;
import org.example.hanmo.service.AdminService;
import org.example.hanmo.service.MatchingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "관리자 추가 정보 입력",tags = {"관리자 로그인"})
    @PutMapping("/signup")
    public ResponseEntity<String> addAdminInfo(@RequestBody AdminRequestDto dto) {
        adminService.addAdminInfo(dto);
        return ResponseEntity.ok("추가 정보가 입력되었습니다.");
    }

    @Operation(summary = "관리자 로그인 (테스트용입니다. jwt나올 시 변경, 지금은 임시토큰)",tags = {"관리자 로그인"})
    @PostMapping("/login")
    public ResponseEntity<String> loginAdmin(@RequestBody AdminRequestDto request) {
        String tempToken = adminService.loginAdmin(request);
        return ResponseEntity.ok().header("tempToken", tempToken).body("관리자 로그인 되었습니다.");
    }

    @Operation(summary = "상태값 초기화", tags = {"관리자 기능"})
    @PatchMapping("/reset-matching/{userId}")
    public ResponseEntity<String> resetUserMatchingInfo(@PathVariable Long userId) {
        adminService.resetUserMatchingInfo(userId);
        return ResponseEntity.ok(userId+"번 유저의 상태가 초기화되었습니다.");
    }

    @Operation(summary = "닉네임·이름으로 사용자 검색 (페이지당 30개)", tags = {"관리자 기능"})
    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<AdminUserResponseDto>> searchUsers(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "status", required = false) UserStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, 30);
        var userPage = adminService.searchUsersByNickname( keyword,status, pageable);
        return ResponseEntity.ok(PageResponseDto.from(userPage));
    }

    @Operation(summary = "닉네임으로 사용자 삭제 (관리자)",tags = {"관리자 기능"})
    @DeleteMapping("/{nickname}")
    public ResponseEntity<String> deleteUser(@PathVariable String nickname) {
        adminService.deleteUserByNickname(nickname);
        return ResponseEntity.ok("입력하신 닉네임 : " + nickname +" 이 삭제 되었습니다.");
    }


    @Operation(summary = "오늘,총 매칭된 그룹 수_ 지금은 일반유저도 조회 가능함",tags = {"관리자 기능"})
    @GetMapping("/matching-count")
    public ResponseEntity<DashboardGroupDto> getDashboardStats(HttpServletRequest request) {
        String tempToken = request.getHeader("tempToken");
        DashboardGroupDto matchingCount = adminService.getDashboardStats(tempToken);
        return ResponseEntity.ok(matchingCount);
    }

    @Operation(summary = "오늘 가입한 회원 수",tags = {"관리자 기능"})
    @GetMapping("/signup-count")
    public ResponseEntity<DashboardSignUpDto> getTodaySignupStats() {
        DashboardSignUpDto signUpCount=adminService.getTodaySignupStats();
        return ResponseEntity.ok(signUpCount);
    }

    @Operation(summary = "사용자 권한 변경 (0=USER, 1=ADMIN)", tags = {"관리자 기능"})
    @PutMapping("/role")
    public ResponseEntity<String> changeUserRole(@RequestBody AdminRoleRequestDto dto) {
        adminService.changeUserRole(dto.getUserId(), dto.getNewRole());
        return ResponseEntity.ok("유저 "+dto.getUserId()+"번 의 등급이 "+ dto.getNewRole()+" 로 변경되었습니다.");
    }

    @Operation(summary = "매칭 대기열 현황 조회", tags = {"관리자 기능"})
    @GetMapping("/queue-status")
    public ResponseEntity<List<QueueInfoResponseDto>> getQueueStatuses() {
        List<QueueInfoResponseDto> statuses = adminService.getQueueStatuses();
        return ResponseEntity.ok(statuses);
    }


    @Operation(summary = "사용자 수동 매칭", tags = {"관리자 기능"})
    @PostMapping("/manual-match")
    public ResponseEntity<AdminMatchingResponseDto> manualMatch(@RequestBody ManualMatchRequestDto manual) {
        AdminMatchingResponseDto resp = adminService.matchUsersManually( manual);
        return ResponseEntity.ok(resp);
    }
}