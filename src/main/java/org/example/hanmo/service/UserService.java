package org.example.hanmo.service;

import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.dto.user.response.UserSignUpResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserSignUpResponseDto signUpUser(UserSignUpRequestDto signUpRequestDto);
    UserSignUpResponseDto changeNickname(String tempToken);
}
