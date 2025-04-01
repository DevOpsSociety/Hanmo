package org.example.hanmo.vaildate;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.error.exception.TempTokenException;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.repository.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthValidate {
    private final RedisTempRepository redisTempRepository;
    private final UserRepository userRepository;

    public UserEntity validateTempToken(String tempToken) {
        String phoneNumber = redisTempRepository.getPhoneNumberByTempToken(tempToken);
        if (phoneNumber == null) {
            throw new TempTokenException("유효하지 않은 토큰입니다.", ErrorCode.INVALID_CODE_EXCEPTION);
        }
        return userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(
                        () ->
                                new NotFoundException(
                                        "사용자를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));
    }
}
