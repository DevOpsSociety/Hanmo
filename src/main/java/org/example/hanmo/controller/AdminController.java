package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.admin.date.DashboardSignUpDto;
import org.example.hanmo.dto.admin.date.DashboardGroupDto;
import org.example.hanmo.dto.admin.date.QueueInfoResponseDto;
import org.example.hanmo.dto.admin.request.AdminRequestDto;
import org.example.hanmo.dto.admin.request.AdminRoleRequestDto;
import org.example.hanmo.dto.admin.response.AdminUserResponseDto;
import org.example.hanmo.dto.admin.response.PageResponseDto;
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
    private final MatchingService matchingService;

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

    @Operation(summary = "닉네임·이름으로 사용자 검색 (페이지당 30개)", tags = {"관리자 기능"})
    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<AdminUserResponseDto>> searchUsers(HttpServletRequest request,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        String tempToken = request.getHeader("tempToken");
        Pageable pageable = PageRequest.of(page, 30);
        var userPage = adminService.searchUsersByNickname(tempToken, keyword, pageable);
        return ResponseEntity.ok(PageResponseDto.from(userPage));
    }

    @Operation(summary = "닉네임으로 사용자 삭제 (관리자)",tags = {"관리자 기능"})
    @DeleteMapping("/{nickname}")
    public ResponseEntity<String> deleteUser(HttpServletRequest request, @PathVariable String nickname) {
        String tempToken = request.getHeader("tempToken");
        adminService.deleteUserByNickname(tempToken, nickname);
        return ResponseEntity.ok("입력하신 닉네임 : " + nickname +" 이 삭제 되었습니다.");
    }


    @Operation(summary = "오늘 매칭된 그룹 수",tags = {"관리자 기능"})
    @GetMapping("/matching-count")
    public ResponseEntity<DashboardGroupDto> getDashboardStats(HttpServletRequest request) {
        String tempToken = request.getHeader("tempToken");
        DashboardGroupDto matchingCount = adminService.getDashboardStats(tempToken);
        return ResponseEntity.ok(matchingCount);
    }

    @Operation(summary = "오늘 가입한 회원 수",tags = {"관리자 기능"})
    @GetMapping("/signup-count")
    public ResponseEntity<DashboardSignUpDto> getTodaySignupStats(HttpServletRequest request) {
        String tempToken = request.getHeader("tempToken");
        DashboardSignUpDto signUpCount=adminService.getTodaySignupStats(tempToken);
        return ResponseEntity.ok(signUpCount);
    }

    @Operation(summary = "사용자 권한 변경 (0=USER, 1=ADMIN)", tags = {"관리자 기능"})
    @PutMapping("/role")
    public ResponseEntity<String> changeUserRole(HttpServletRequest request, @RequestBody AdminRoleRequestDto dto) {
        String tempToken = request.getHeader("tempToken");
        adminService.changeUserRole(tempToken, dto.getUserId(), dto.getNewRole());
        return ResponseEntity.ok("유저 "+dto.getUserId()+"번 의 등급이 "+ dto.getNewRole()+" 로 변경되었습니다.");
    }

    @Operation(summary = "매칭 대기열 현황 조회", tags = {"관리자 기능"})
    @GetMapping("/queue-status")
    public ResponseEntity<List<QueueInfoResponseDto>> getQueueStatuses(HttpServletRequest request) {
        String tempToken = request.getHeader("tempToken");
        List<QueueInfoResponseDto> statuses = adminService.getQueueStatuses(tempToken);
        return ResponseEntity.ok(statuses);
    }
}
