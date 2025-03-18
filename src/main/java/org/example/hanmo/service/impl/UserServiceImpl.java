package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.dto.user.response.UserSignUpResponseDto;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.UserService;
import org.example.hanmo.vaildate.SmsValidate;
import org.example.hanmo.vaildate.UserValidate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RedisSmsRepository redisSmsRepository;
    @Override
    public UserSignUpResponseDto signUpUser(UserSignUpRequestDto signUpRequestDto) {
        String phoneNumber=signUpRequestDto.getPhoneNumber();
//        SmsValidate.validateSignUp(phoneNumber, redisSmsRepository, userRepository);

        UserEntity user = signUpRequestDto.SignUpToUserEntity();
        UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository);
//        redisSmsRepository.deleteVerifiedFlag(phoneNumber);
        userRepository.save(user);
        return new UserSignUpResponseDto(user.getNickname(), user.getPhoneNumber());
    }

    @Override
    public UserEntity changeNickname(String phoneNumber) {
        return null;
    }
}
