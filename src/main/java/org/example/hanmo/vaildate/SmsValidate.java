package org.example.hanmo.vaildate;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.WithdrawalStatus;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.AccountDeactivatedException;
import org.example.hanmo.error.exception.SmsSendException;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.repository.UserRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SmsValidate {

  public static void validateSmsCodeExistence(
      String certificationCode, RedisSmsRepository redisSmsRepository) {
    String key = "smsCode:" + certificationCode;
    String value = redisSmsRepository.getSmsCertification(certificationCode);
    System.out.println("Redis Key: " + key + ", Value: " + value);
    if (value == null || value.trim().isEmpty()) {
      throw new SmsSendException("인증번호가 만료되었습니다.", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
    }
  }

  public static void validateSmsCodeNotNull(String phoneNumber) {
    if (phoneNumber == null) {
      throw new SmsSendException("인증번호가 유효하지 않습니다.", ErrorCode.SMS_INVALID_CODE_EXCEPTION);
    }
  }

  public static void validateDuplicatePhoneNumber(
      String phoneNumber, UserRepository userRepository) {
    if (userRepository.existsByPhoneNumber(phoneNumber)) {
      // 이미 해당 전화번호로 등록된 사용자가 있으므로, 실제 UserEntity 객체를 조회하여 상태 확인
      UserEntity user = userRepository.findByPhoneNumber(phoneNumber).get();
      if (user.getWithdrawalStatus() == WithdrawalStatus.WITHDRAWN) {
        throw new AccountDeactivatedException(
            "휴면 상태입니다.", ErrorCode.ALREADY_DORMANT_ACCOUNT_EXCEPTION);
      }
      throw new SmsSendException("이미 회원입니다.", ErrorCode.DUPLICATE_PHONE_NUMBER_EXCEPTION);
    }
  }

  public static void validateSignUp(
      String phoneNumber, RedisSmsRepository redisSmsRepository, UserRepository userRepository) {
    if (!redisSmsRepository.isVerifiedFlag(phoneNumber)) {
      throw new SmsSendException("인증이 완료되지 않았습니다.", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
    }
    if (userRepository.existsByPhoneNumber(phoneNumber)) {
      throw new SmsSendException("이미 회원입니다.", ErrorCode.DUPLICATE_PHONE_NUMBER_EXCEPTION);
    }
  }

  // 인증과 , 임시토큰 함께 검증
  public static String validateSmsCodeByCode(
      String certificationCode, RedisSmsRepository redisSmsRepository) {
    String phoneNumber = redisSmsRepository.getSmsCertification(certificationCode);
    if (phoneNumber == null) {
      throw new SmsSendException(
          "인증번호가 만료되었거나 일치하지 않습니다.", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
    }
    return phoneNumber;
  }
}
