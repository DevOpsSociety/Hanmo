package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.admin.date.DashboardSignUpDto;
import org.example.hanmo.dto.admin.date.DashboardGroupDto;
import org.example.hanmo.dto.admin.request.AdminRequestDto;
import org.example.hanmo.dto.admin.response.AdminUserResponseDto;
import org.example.hanmo.service.AdminService;
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

    @Operation(summary = "닉네임으로 사용자 검색 (최대 30개)",tags = {"관리자 기능"})
    @GetMapping("/search")
    public ResponseEntity<List<AdminUserResponseDto>> searchUsers(HttpServletRequest request, @RequestParam(value = "nickname", required = false, defaultValue = "") String nickname) {
        String tempToken = request.getHeader("tempToken");
        List<AdminUserResponseDto> result = adminService.searchUsersByNickname(tempToken, nickname);
        return ResponseEntity.ok(result);
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
}
