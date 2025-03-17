package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.sms.request.SmsRequestDto;
import org.example.hanmo.dto.sms.request.SmsVerifyRequestDto;
import org.example.hanmo.service.SmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    @Operation(summary = "전화번호 인증 SMS전송")
    @PostMapping("/send")
    public ResponseEntity<?> SendSMS(@RequestBody @Valid SmsRequestDto smsRequestDto){
        smsService.sendSms(smsRequestDto);
        return ResponseEntity.ok("인증번호를 전송했습니다.");
    }

    @Operation(summary = "인증코드 확인")
    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody @Valid SmsVerifyRequestDto smsVerifyDto) {
        smsService.verifyCode(smsVerifyDto);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}
