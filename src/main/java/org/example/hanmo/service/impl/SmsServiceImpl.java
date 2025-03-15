package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.sms.SmsRequestDto;
import org.example.hanmo.service.SmsService;
import org.example.hanmo.util.SmsCretificationUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    private final SmsCretificationUtil smsCretificationUtil;

    @Override
    public void SendSms(SmsRequestDto smsRequestDto) {
        String phoneNumber = smsRequestDto.getPhoneNumber();
        String certificationCode= Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000);
        smsCretificationUtil.sendSMS(phoneNumber, certificationCode);
    }
}
