package org.example.hanmo.controller;

import jakarta.validation.Valid;

import org.example.hanmo.dto.sms.request.SmsRequestDto;
import org.example.hanmo.dto.sms.request.SmsVerifyRequestDto;
import org.example.hanmo.service.SmsService;
import org.example.hanmo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;
    private final UserService userService;

    @Operation(summary = "문자 SMS전송 API")
    @PostMapping("/send")
    public ResponseEntity<?> SendSMS(@Valid @RequestBody SmsRequestDto smsRequestDto) {
        smsService.sendSms(smsRequestDto);
        return ResponseEntity.ok("인증번호를 전송했습니다.");
    }

    @Operation(summary = "SMS 인증번호 검증 API")
    @PostMapping("/verify")
    public ResponseEntity<String> verifySmsCode(
            @Valid @RequestBody SmsVerifyRequestDto requestDto) {
        smsService.verifyCode(requestDto.getCertificationCode());
        return ResponseEntity.ok("SMS 인증이 완료되었습니다.");
    }

    @Operation(summary = "계정 복구용 SMS 전송 API")
    @PostMapping("/send")
    public ResponseEntity<String> sendRestoreSms(@Valid @RequestBody SmsRequestDto smsRequestDto) {
        smsService.sendRestoreSms(smsRequestDto);
        return ResponseEntity.ok("계정 복구용 인증번호를 전송했습니다.");
    }

    @Operation(summary = "복구용 SMS 인증번호 검증 및 계정 복구 API")
    @PostMapping("/verify")
    public ResponseEntity<String> verifyRestoreSms(
            @Valid @RequestBody SmsVerifyRequestDto requestDto) {
        String verifiedPhone = smsService.verifyRestoreCode(requestDto.getCertificationCode());
        userService.restoreUserAccount(verifiedPhone);
        return ResponseEntity.ok("계정 복구가 완료되었습니다.");
    }
}
