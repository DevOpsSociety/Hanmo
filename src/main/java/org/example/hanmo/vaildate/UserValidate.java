package org.example.hanmo.vaildate;

import org.apache.commons.lang3.StringUtils;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.BadRequestException;
import org.example.hanmo.error.exception.ForbiddenException;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.util.RandomNicknameUtil;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserValidate {

    private final UserRepository userRepository;

    public static void validateDuplicateNickname(String nickname, UserRepository userRepository) {
        if (StringUtils.isNotBlank(nickname) && userRepository.existsByNickname(nickname)) {
            throw new BadRequestException(
                    "409_Error, 이미 사용중인 닉네임입니다.", ErrorCode.DUPLICATE_NICKNAME_EXCEPTION);
        }
    }

    public static void setUniqueRandomNicknameIfNeeded(
            UserEntity user, boolean regenerate, UserRepository userRepository) {
        if (regenerate || user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            String uniqueNickname =
                    java.util.stream.Stream.generate(
                                    () -> RandomNicknameUtil.generateNickname(user.getDepartment()))
                            .filter(nickname -> !userRepository.existsByNickname(nickname))
                            .findFirst()
                            .orElseThrow(
                                    () ->
                                            new BadRequestException(
                                                    "409", ErrorCode.DUPLICATE_NICKNAME_EXCEPTION));
            user.setNickname(uniqueNickname);
        }
    }

    public static UserEntity getUserByPhoneNumber(
            String phoneNumber, UserRepository userRepository) {
        return userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(
                        () ->
                                new NotFoundException(
                                        "404_Error, 사용자를 찾을 수 없습니다.",
                                        ErrorCode.NOT_FOUND_EXCEPTION));
    }

    public static String validatePhoneNumberByTempToken(
            String tempToken, RedisTempRepository redisTempRepository) {
        String phoneNumber = redisTempRepository.getPhoneNumberByTempToken(tempToken);
        if (phoneNumber == null) {
            throw new ForbiddenException("400_Error", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
        }
        return phoneNumber;
    }

    public UserEntity findByPhoneNumberAndStudentNumber(String phoneNumber, String studentNumber) {
        return userRepository
                .findByPhoneNumberAndStudentNumber(phoneNumber, studentNumber)
                .orElseThrow(
                        () ->
                                new NotFoundException(
                                        "사용자를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));
    }

    public static void validateNicknameNotChanged(UserEntity user) {
        if (user.isNicknameChanged()) {
            throw new BadRequestException(
                    "이미 닉네임이 변경되었습니다.", ErrorCode.DUPLICATE_NICKNAME_EXCEPTION);
        }
    }
}
