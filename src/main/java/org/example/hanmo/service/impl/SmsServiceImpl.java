package org.example.hanmo.service.impl;

import org.example.hanmo.dto.sms.request.SmsRequestDto;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.SmsService;
import org.example.hanmo.util.SmsCertificationUtil;
import org.example.hanmo.vaildate.SmsValidate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

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

    redisSmsRepository.createSmsCertification(phoneNum, certificationCode);
  } // 5분 동안 인증 코드 저장 (해당 전화번호에 대해)

  @Transactional
  public void verifyCode(String certificationCode) {
    String phoneNumber = SmsValidate.validateSmsCodeByCode(certificationCode, redisSmsRepository);
    SmsValidate.validateSmsCodeExistence(certificationCode, redisSmsRepository);
    redisSmsRepository.deleteSmsCertification(certificationCode);
    redisSmsRepository.setVerifiedFlag(phoneNumber);
  }

  @Override
  public boolean isVerify(String phoneNumber, String certificationCode) {
    String storedCode = redisSmsRepository.getSmsCertification(phoneNumber);
    return storedCode != null && storedCode.equals(certificationCode);
  }

  @Override
  public void sendRestoreSms(SmsRequestDto smsRequestDto) {
    String phoneNum = smsRequestDto.getPhoneNumber();
    String certificationCode = generateCertificationCode();
    smsCertificationUtil.sendSMS(phoneNum, certificationCode);
    redisSmsRepository.createRestoreSmsCertification(phoneNum, certificationCode);
  }

  @Override
  public String verifyRestoreCode(String certificationCode) {
    String phoneNumber = redisSmsRepository.getRestoreSmsCertification(certificationCode);
    SmsValidate.validateSmsCodeNotNull(phoneNumber);
    redisSmsRepository.deleteRestoreSmsCertification(certificationCode);
    return phoneNumber;
  }

  private String generateCertificationCode() {
    int code = (int) (Math.random() * (999999 - 100000 + 1)) + 100000;
    return Integer.toString(code);
  } // 헬퍼 메서드: 6자리 인증 코드 생성
}
