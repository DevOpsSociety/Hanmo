package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.dto.user.response.UserSignUpResponseDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.ForbiddenException;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.UserService;
import org.example.hanmo.vaildate.SmsValidate;
import org.example.hanmo.vaildate.UserValidate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RedisSmsRepository redisSmsRepository;
    private final RedisTempRepository redisTempRepository;
    @Override
    public UserSignUpResponseDto signUpUser(UserSignUpRequestDto signUpRequestDto) {
        String phoneNumber=signUpRequestDto.getPhoneNumber();
        SmsValidate.validateSignUp(phoneNumber, redisSmsRepository, userRepository);

        UserEntity user = signUpRequestDto.SignUpToUserEntity();
        UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository);
//        redisSmsRepository.deleteVerifiedFlag(phoneNumber);
        String tempToken = UUID.randomUUID().toString();
        redisTempRepository.setTempToken(phoneNumber, tempToken, 5 * 60);
        userRepository.save(user);

        return new UserSignUpResponseDto(user.getNickname(), user.getPhoneNumber(),tempToken);
    }

    @Override
    @Transactional
    public UserSignUpResponseDto changeNickname(String tempToken) {
        // SMS 인증 완료 플래그 존재 여부 확인 (5분 내에만 허용)
        String phoneNumber = redisTempRepository.getPhoneNumberByTempToken(tempToken);
        if (phoneNumber == null) {
            throw new ForbiddenException("400_Error", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
        }

        // 해당 전화번호의 사용자 조회
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ForbiddenException("404_Error", ErrorCode.NOT_FOUND_EXCEPTION));

        // 중복 없이 새로운 랜덤 닉네임 재생성
        UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository);

        // 재설정 후 임시 토큰 삭제 (재요청 제한)
        redisTempRepository.deleteTempToken(tempToken);

        user = userRepository.save(user);
        return new UserSignUpResponseDto(user.getNickname(), user.getPhoneNumber(), null);
    }
}
