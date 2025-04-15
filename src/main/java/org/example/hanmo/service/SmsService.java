package org.example.hanmo.service;

import org.example.hanmo.dto.sms.request.SmsRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface SmsService {
    void sendSms(SmsRequestDto smsRequestDto);

    void verifyCode(String certificationCode);

    boolean isVerify(String phoneNumber, String certificationCode);

    // 복구용 메서드
    void sendRestoreSms(SmsRequestDto smsRequestDto);

    String verifyRestoreCode(String certificationCode);
}
