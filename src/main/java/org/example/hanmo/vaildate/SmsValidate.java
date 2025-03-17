package org.example.hanmo.vaildate;

import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.SmsSendException;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.repository.UserRepository;

public class SmsValidate {

    public static void validateSmsCodeExistence(String phoneNumber, RedisSmsRepository redisSmsRepository) {
        if (!redisSmsRepository.hasKey(phoneNumber)) {
            throw new SmsSendException("400_Error_인증코드가 만료되었습니다.", ErrorCode.SMS_CODE_EXPIRED_EXCEPTION);
        }
    }

    public static void validateSmsCodeMatch(String phoneNumber, String inputCode, RedisSmsRepository redisSmsRepository) {
        String storedCode = redisSmsRepository.getSmsCertification(phoneNumber);
        if (storedCode == null || !storedCode.equals(inputCode)) {
            throw new SmsSendException("400_Error_인증에 실패하였습니다.", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
        }
    }

    public static void validateDuplicatePhoneNumber(String phoneNumber, UserRepository userRepository) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new SmsSendException("409_Error, 이미 회원입니다.", ErrorCode.DUPLICATE_PHONE_NUMBER_EXCEPTION);
        }
    }

    public static void validateSignUp(String phoneNumber, RedisSmsRepository redisSmsRepository, UserRepository userRepository) {
        if (!redisSmsRepository.isVerifiedFlag(phoneNumber)) {
            throw new SmsSendException("400_Error_인증이 완료되지 않았습니다.", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new SmsSendException("409_Error, 이미 회원입니다.", ErrorCode.DUPLICATE_PHONE_NUMBER_EXCEPTION);
        }
    }
}
