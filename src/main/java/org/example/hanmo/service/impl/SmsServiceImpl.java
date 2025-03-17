package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.sms.request.SmsRequestDto;
import org.example.hanmo.dto.sms.request.SmsVerifyRequestDto;
import org.example.hanmo.dto.sms.response.AuthResponseDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.SmsSendException;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.SmsService;
import org.example.hanmo.util.SmsCertificationUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        // UserTable에 이미 등록되어 있다면 예외 발생
        if (userRepository.existsByPhoneNumber(phoneNum)) {
            throw new SmsSendException("409_Error, 이미 회원입니다.", ErrorCode.DUPLICATE_PHONE_NUMBER_EXCEPTION);
        }
        // 6자리 인증 코드를 랜덤으로 생성
        String certificationCode = generateCertificationCode();
        smsCertificationUtil.sendSMS(phoneNum, certificationCode);

        // 3분 동안 인증 코드 저장 (해당 전화번호에 대해)
        redisSmsRepository.createSmsCertification(phoneNum, certificationCode);
    }

    // 검증 코드 확인 반환 (전화번호와 인증번호 모두 DTO로 받음)
    @Override
    public boolean verifyCode(SmsVerifyRequestDto verifyRequestDto) {
        String phoneNum = verifyRequestDto.getPhoneNumber();
        String inputCode = verifyRequestDto.getCertificationCode();

        if (!redisSmsRepository.hasKey(phoneNum)) {
            throw new SmsSendException("400_Error, 인증코드가 존재하지 않거나 만료되었습니다.", ErrorCode.SMS_CODE_EXPIRED_EXCEPTION);
        }

        if (!isVerify(phoneNum, inputCode)) {
            throw new SmsSendException("400_Error, 인증코드가 일치하지 않습니다.", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
        }

        // 인증 성공 시 Redis에 저장된 인증 정보 삭제
        redisSmsRepository.deleteSmsCertification(phoneNum);
        return true; // 단순히 인증 성공이면 true 반환
    }

    // 헬퍼 메서드: 전화번호와 입력된 인증번호 비교
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
