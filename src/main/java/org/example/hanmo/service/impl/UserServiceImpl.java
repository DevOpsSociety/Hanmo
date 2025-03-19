package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.dto.user.response.UserSignUpResponseDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.ForbiddenException;
import org.example.hanmo.error.exception.NotFoundException;
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
        redisSmsRepository.deleteVerifiedFlag(phoneNumber);
        String tempToken = UUID.randomUUID().toString();
        redisTempRepository.setTempToken(phoneNumber, tempToken, 5 * 60);
        userRepository.save(user);

        return new UserSignUpResponseDto(user.getNickname(), user.getPhoneNumber());
    }

    @Override
    @Transactional
    public UserSignUpResponseDto changeNickname(String tempToken) {
        String phoneNumber = UserValidate.validatePhoneNumberByTempToken(tempToken, redisTempRepository);
        UserEntity user = UserValidate.getUserByPhoneNumber(phoneNumber, userRepository);

        UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository);

        redisTempRepository.deleteTempToken(tempToken);
        user = userRepository.save(user);
        return new UserSignUpResponseDto(user.getNickname(), user.getPhoneNumber());
    }

}
