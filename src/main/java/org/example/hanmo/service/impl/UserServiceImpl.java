package org.example.hanmo.service.impl;

import java.util.UUID;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.user.request.UserLoginRequestDto;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.dto.user.response.UserProfileResponseDto;
import org.example.hanmo.dto.user.response.UserSignUpResponseDto;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.UserService;
import org.example.hanmo.vaildate.AuthValidate;
import org.example.hanmo.vaildate.SmsValidate;
import org.example.hanmo.vaildate.UserValidate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RedisSmsRepository redisSmsRepository;
    private final RedisTempRepository redisTempRepository;
    private final UserValidate userValidate;
    private final AuthValidate authValidate;

    @Override
    public UserSignUpResponseDto signUpUser(UserSignUpRequestDto signUpRequestDto) {
        String phoneNumber = signUpRequestDto.getPhoneNumber();
        // SMS 인증 완료 플래그와 중복 가입 여부를 검증 (전화번호 기준)
        SmsValidate.validateSignUp(phoneNumber, redisSmsRepository, userRepository);

        UserEntity user = signUpRequestDto.SignUpToUserEntity();
        // 랜덤 닉네임
        UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository);
        redisSmsRepository.deleteVerifiedFlag(phoneNumber);
        // 임시 토큰을 생성 (UUID 사용) (TTL 5분)
        String tempToken = UUID.randomUUID().toString();
        redisTempRepository.setTempToken(phoneNumber, tempToken, 5 * 60);
        userRepository.save(user);

        return new UserSignUpResponseDto(user.getNickname(), user.getPhoneNumber());
    }

    @Override
    public UserSignUpResponseDto changeNickname(String tempToken) {
        // 임시 토큰으로부터 전화번호를 검증 및 조회합니다.
        String phoneNumber =
                UserValidate.validatePhoneNumberByTempToken(tempToken, redisTempRepository);
        UserEntity user = UserValidate.getUserByPhoneNumber(phoneNumber, userRepository);

        UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository);

        user = userRepository.save(user);
        return new UserSignUpResponseDto(user.getNickname(), user.getPhoneNumber());
    }

    @Override
    public void withdrawUser(String phoneNumber) {
        UserEntity user = UserValidate.getUserByPhoneNumber(phoneNumber, userRepository);
        userRepository.delete(user);
        redisSmsRepository.deleteVerifiedFlag(phoneNumber);
    } // Redis에 저장된 인증 완료 플래그 삭제 (있을 경우)

    @Override
    public String loginUser(UserLoginRequestDto requestDto) {
        UserEntity user =
                userValidate.findByPhoneNumberAndStudentNumber(
                        requestDto.getPhoneNumber(), requestDto.getStudentNumber());
        String tempToken = redisTempRepository.createTempTokenForUser(user.getPhoneNumber(), true);
        return tempToken;
    }

    @Override
    public UserProfileResponseDto getUserProfile(String tempToken) {
        UserEntity user = authValidate.validateTempToken(tempToken);
        return new UserProfileResponseDto(
                user.getNickname(), user.getName(), user.getInstagramId());
    }
}
