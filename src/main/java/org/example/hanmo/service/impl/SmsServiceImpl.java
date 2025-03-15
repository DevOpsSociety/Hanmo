package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.sms.SmsRequestDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.SmsSendException;
import org.example.hanmo.service.SmsService;
import org.example.hanmo.util.SmsCertificationUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    private final SmsCertificationUtil smsCertificationUtil;

    @Override
    public void SendSms(SmsRequestDto smsRequestDto) {
        String phoneNumber = smsRequestDto.getPhoneNumber();
        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000);
        try {
            smsCertificationUtil.sendSMS(phoneNumber, certificationCode);
        } catch (Exception e) {
            System.err.println("SMS 전송 실패: " + e.getMessage());
            throw new SmsSendException("SMS 전송에 실패했습니다.", ErrorCode.SMS_SENDER_EXCEPTION);
        }
    }

}
