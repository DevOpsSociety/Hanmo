package org.example.hanmo.service;

import org.example.hanmo.dto.sms.request.SmsRequestDto;
import org.example.hanmo.dto.sms.request.SmsVerifyRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface SmsService {
    void sendSms(SmsRequestDto smsRequestDto);
    boolean verifyCode(SmsVerifyRequestDto verifyRequestDto);
    boolean isVerify(String phoneNumber,String certificationCode);
}
