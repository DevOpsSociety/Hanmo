package org.example.hanmo.service;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;

public interface UserService {
    UserEntity signUpUser(UserSignUpRequestDto signUpRequestDto);
}
