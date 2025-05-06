package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.admin.request.AdminRequestDto;
import org.example.hanmo.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "관리자 추가 정보 입력")
    @PutMapping("/signup/admin")
    public ResponseEntity<String> addAdminInfo(@RequestBody AdminRequestDto dto) {
        adminService.addAdminInfo(dto);
        return ResponseEntity.ok("추가 정보가 입력되었습니다.");
    }

    @Operation(summary = "관리자 로그인 (테스트용입니다. jwt나올 시 변경, 지금은 임시토큰)")
    @PostMapping("/login/admin")
    public ResponseEntity<String> loginAdmin(@RequestBody AdminRequestDto request) {
        String tempToken = adminService.loginAdmin(request);
        return ResponseEntity.ok().header("tempToken", tempToken).body("관리자 로그인 되었습니다.");
    }
}
