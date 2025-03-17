package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserSignUpRequestDto requestDto) {
        userService.signUpUser(requestDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
}
