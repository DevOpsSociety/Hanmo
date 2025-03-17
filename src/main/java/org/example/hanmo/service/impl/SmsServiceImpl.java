package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.sms.request.SmsRequestDto;
import org.example.hanmo.dto.sms.request.SmsVerifyRequestDto;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.SmsService;
import org.example.hanmo.util.SmsCertificationUtil;
import org.example.hanmo.vaildate.SmsValidate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final SmsCertificationUtil smsCertificationUtil;
    private final UserRepository userRepository;
    private final RedisSmsRepository redisSmsRepository;

    @Override
    public void sendSms(SmsRequestDto smsRequestDto) {
        String phoneNum = smsRequestDto.getPhoneNumber();

        SmsValidate.validateDuplicatePhoneNumber(phoneNum, userRepository);
        String certificationCode = generateCertificationCode();
        smsCertificationUtil.sendSMS(phoneNum, certificationCode);

        // 3분 동안 인증 코드 저장 (해당 전화번호에 대해)
        redisSmsRepository.createSmsCertification(phoneNum, certificationCode);
    }

    @Override
    public boolean verifyCode(SmsVerifyRequestDto verifyRequestDto) {
        String phoneNum = verifyRequestDto.getPhoneNumber();
        String inputCode = verifyRequestDto.getCertificationCode();

        SmsValidate.validateSmsCodeExistence(phoneNum, redisSmsRepository);
        SmsValidate.validateSmsCodeMatch(phoneNum, inputCode, redisSmsRepository);
        SmsValidate.validateDuplicatePhoneNumber(phoneNum, userRepository);

        // 검증에 성공하면 Redis에 저장된 인증 코드 삭제
        redisSmsRepository.deleteSmsCertification(phoneNum);
        return true;
    }

    @Override
    public boolean isVerify(String phoneNumber, String certificationCode) {
        String storedCode = redisSmsRepository.getSmsCertification(phoneNumber);
        return storedCode != null && storedCode.equals(certificationCode);
    }

    // 헬퍼 메서드: 6자리 인증 코드 생성
    private String generateCertificationCode() {
        int code = (int)(Math.random() * (999999 - 100000 + 1)) + 100000;
        return Integer.toString(code);
    }
}
