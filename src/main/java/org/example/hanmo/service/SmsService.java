package org.example.hanmo.service;

import org.example.hanmo.dto.sms.SmsRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface SmsService {
    void SendSms(SmsRequestDto smsRequestDto);
}
