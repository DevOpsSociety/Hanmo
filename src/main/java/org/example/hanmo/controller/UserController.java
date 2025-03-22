package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.user.request.UserLoginRequestDto;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.dto.user.response.UserSignUpResponseDto;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RedisTempRepository redisTempRepository;

    @Operation(summary = "간편 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<UserSignUpResponseDto> signup(@RequestBody UserSignUpRequestDto request) {
        UserSignUpResponseDto responseDto = userService.signUpUser(request);
        String tempToken = redisTempRepository.createTempTokenForUser(responseDto.getPhoneNumber(),false);
        return ResponseEntity.ok()
                .header("tempToken", tempToken)
                .body(responseDto);
    }

    @Operation(summary = "1회 닉네임 변경")
    @PutMapping("/nickname")
    public ResponseEntity<UserSignUpResponseDto> changeNickname(HttpServletRequest request) {
        String tempToken = request.getHeader("tempToken");
        UserSignUpResponseDto response = userService.changeNickname(tempToken);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 정보 삭제")
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdrawUser(@RequestParam String phoneNumber) {
        userService.withdrawUser(phoneNumber);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    @Operation(summary = "간편 로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDto requestDto) {
        String tempToken=userService.loginUser(requestDto);
        return ResponseEntity.ok().header("tempToken",tempToken).body("로그인 되었습니다.");
    }
}
