package org.example.hanmo.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.hanmo.dto.user.request.UserLoginRequestDto;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.dto.user.response.UserProfileResponseDto;
import org.example.hanmo.dto.user.response.UserSignUpResponseDto;

import java.io.IOException;

public interface UserService {
    UserSignUpResponseDto signUpUser(UserSignUpRequestDto signUpRequestDto);
    UserSignUpResponseDto changeNickname(String tempToken);

    void withdrawUser(String phoneNumber);
    String loginUser(UserLoginRequestDto requestDto);

    UserProfileResponseDto getUserProfile(String tempToken);
}
