package org.example.hanmo.vaildate;

import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.SmsSendException;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.repository.UserRepository;

public class SmsValidate {
    // Sms 검증 메서드
    public static void validateSmsCodeExistence(String phoneNumber, RedisSmsRepository redisSmsRepository) {
        if (!redisSmsRepository.hasKey(phoneNumber)) {
            throw new SmsSendException("400_Error", ErrorCode.SMS_CODE_EXPIRED_EXCEPTION);
        }
    }

    public static void validateSmsCodeMatch(String phoneNumber, String inputCode, RedisSmsRepository redisSmsRepository) {
        String storedCode = redisSmsRepository.getSmsCertification(phoneNumber);
        if (storedCode == null) {
            throw new SmsSendException("400_Error", ErrorCode.SMS_CODE_EXPIRED_EXCEPTION);
        }
        if (!storedCode.equals(inputCode)) {
            throw new SmsSendException("400_Error", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
        }
    }

    public static void validateDuplicatePhoneNumber(String phoneNumber, UserRepository userRepository) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new SmsSendException("409_Error, 이미 회원입니다.", ErrorCode.DUPLICATE_PHONE_NUMBER_EXCEPTION);
        }
    }

    public static void validateSmsVerification(String phoneNumber, String inputCode, RedisSmsRepository redisSmsRepository, UserRepository userRepository) {
        validateSmsCodeExistence(phoneNumber, redisSmsRepository);
        validateSmsCodeMatch(phoneNumber, inputCode, redisSmsRepository);
        validateDuplicatePhoneNumber(phoneNumber, userRepository);
    }
}
