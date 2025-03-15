package org.example.hanmo.smsservice;

import org.example.hanmo.dto.sms.SmsRequestDto;
import org.example.hanmo.service.impl.SmsServiceImpl;
import org.example.hanmo.util.SmsCertificationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SmsServiceTest {

    @Mock
    private SmsCertificationUtil smsCertificationUtil;

    @InjectMocks
    private SmsServiceImpl smsServiceImpl;

    @Test
    void SmsSenderTest_인증번호_발송_정상작동_되는지() {
        //given
        String phoneNumber = "01087206548";
        SmsRequestDto dto = new SmsRequestDto(phoneNumber);

        //when
        smsServiceImpl.SendSms(dto);

        //then
        verify(smsCertificationUtil, times(1)).sendSMS(eq(phoneNumber),
                argThat(code -> code.matches("\\d{6}")));
    }
}
