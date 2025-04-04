package org.example.hanmo.vaildate;

import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.SmsSendException;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.repository.UserRepository;

public class SmsValidate {

    public static void validateSmsCodeExistence(
            String certificationCode, RedisSmsRepository redisSmsRepository) {
        String key = "smsCode:" + certificationCode;
        String value = redisSmsRepository.getSmsCertification(certificationCode);
        System.out.println("Redis Key: " + key + ", Value: " + value);
        if (value == null || value.trim().isEmpty()) {
            throw new SmsSendException(
                    "400_Error, 인증번호가 만료되었습니다.", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
        }
    }

    public static void validateDuplicatePhoneNumber(
            String phoneNumber, UserRepository userRepository) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new SmsSendException(
                    "409_Error, 이미 회원입니다.", ErrorCode.DUPLICATE_PHONE_NUMBER_EXCEPTION);
        }
    }

    public static void validateSignUp(
            String phoneNumber,
            RedisSmsRepository redisSmsRepository,
            UserRepository userRepository) {
        if (!redisSmsRepository.isVerifiedFlag(phoneNumber)) {
            throw new SmsSendException(
                    "400_Error_인증이 완료되지 않았습니다.", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new SmsSendException(
                    "409_Error, 이미 회원입니다.", ErrorCode.DUPLICATE_PHONE_NUMBER_EXCEPTION);
        }
    }

    // 인증과 , 임시토큰 함께 검증
    public static String validateSmsCodeByCode(
            String certificationCode, RedisSmsRepository redisSmsRepository) {
        String phoneNumber = redisSmsRepository.getSmsCertification(certificationCode);
        if (phoneNumber == null) {
            throw new SmsSendException(
                    "400_Error, 인증번호가 만료되었거나 일치하지 않습니다.",
                    ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
        }
        return phoneNumber;
    }
}
