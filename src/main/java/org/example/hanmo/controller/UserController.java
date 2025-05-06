package org.example.hanmo.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.example.hanmo.dto.user.request.UserLoginRequestDto;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.dto.user.response.UserProfileResponseDto;
import org.example.hanmo.dto.user.response.UserSignUpResponseDto;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final RedisTempRepository redisTempRepository;

  @Operation(summary = "간편 회원가입",tags = {"유저 기능"})
  @PostMapping("/signup")
  public ResponseEntity<UserSignUpResponseDto> signup(@RequestBody UserSignUpRequestDto request) {
    UserSignUpResponseDto responseDto = userService.signUpUser(request);
    String tempToken =
        redisTempRepository.createTempTokenForUser(responseDto.getPhoneNumber(), false);
    return ResponseEntity.ok().header("tempToken", tempToken).body(responseDto);
  }

  @Operation(summary = "1회 닉네임 변경",tags = {"유저 기능"})
  @PutMapping("/nickname")
  public ResponseEntity<UserSignUpResponseDto> changeNickname(HttpServletRequest request) {
    String tempToken = request.getHeader("tempToken");
    UserSignUpResponseDto response = userService.changeNickname(tempToken);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "회원 탈퇴 (휴면 처리 및 3일 복구 가능)",tags = {"유저 기능"})
  @DeleteMapping("/withdraw")
  public ResponseEntity<String> withdraw(@RequestParam String phoneNumber) {
    // 회원 탈퇴의 경우 DB에서 계정을 삭제(혹은 상태 전환) 처리합니다.
    userService.withdrawUser(phoneNumber);
    return ResponseEntity.ok("회원 탈퇴(휴면) 처리가 완료되었습니다.");
  }

  //    @Operation(summary = "계정 복구 (탈퇴 후 3일 이내 복구 가능)")
  //    @PostMapping("/restore")
  //    public ResponseEntity<String> restore(@RequestParam String phoneNumber) {
  //        userService.restoreUserAccount(phoneNumber);
  //        return ResponseEntity.ok("계정 복구가 완료되었습니다.");
  //    } //sms 복구로 변경

  @Operation(summary = "간편 로그인",tags = {"유저 기능"})
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody UserLoginRequestDto request) {
    String tempToken = userService.loginUser(request);
    return ResponseEntity.ok().header("tempToken", tempToken).body("로그인 되었습니다.");
  }

  @Operation(summary = "내 정보 조회",tags = {"유저 기능"})
  @GetMapping("/profile")
  public ResponseEntity<UserProfileResponseDto> getUserProfile(HttpServletRequest request) {
    String tempToken = request.getHeader("tempToken");
    UserProfileResponseDto getUserDto = userService.getUserProfile(tempToken);
    return ResponseEntity.ok(getUserDto);
  }

  @Operation(summary = "로그아웃",tags = {"유저 기능"})
  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    String tempToken = request.getHeader("tempToken");
    userService.logout(tempToken);
    return ResponseEntity.ok("로그아웃 되었습니다.");
  }
}
