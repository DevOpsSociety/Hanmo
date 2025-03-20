package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.sms.request.SmsRequestDto;
import org.example.hanmo.dto.sms.request.SmsVerifyRequestDto;
import org.example.hanmo.service.SmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    @Operation(summary = "문자 SMS전송 API")
    @PostMapping("/send")
    public ResponseEntity<?> SendSMS(@Valid @RequestBody SmsRequestDto smsRequestDto){
        smsService.sendSms(smsRequestDto);
        return ResponseEntity.ok("인증번호를 전송했습니다.");
    }

    @Operation(summary = "SMS 인증번호 검증 API")
    @PostMapping("/verify")
    public ResponseEntity<String> verifySmsCode(@Valid @RequestBody SmsVerifyRequestDto requestDto) {
        smsService.verifyCode(requestDto.getCertificationCode());
        return ResponseEntity.ok("SMS 인증이 완료되었습니다.");
    }

}
