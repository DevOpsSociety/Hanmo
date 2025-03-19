package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.dto.user.response.UserSignUpResponseDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.UserService;
import org.example.hanmo.vaildate.UserValidate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RedisSmsRepository redisSmsRepository;
    private final UserRepository userRepository;
    private final RedisTempRepository redisTempRepository;

    @PostMapping("/api/user/signup")
    public ResponseEntity<UserSignUpResponseDto> signup(@RequestBody UserSignUpRequestDto request) {
        UserSignUpResponseDto responseDto = userService.signUpUser(request);
        String tempToken = redisTempRepository.createTempTokenForUser(responseDto.getPhoneNumber());

        return ResponseEntity.ok()
                .header("tempToken", tempToken)
                .body(responseDto);
    }


    @PutMapping("/api/user/nickname")
    public ResponseEntity<UserSignUpResponseDto> changeNickname(HttpServletRequest request) {
        String tempToken = request.getHeader("tempToken");
        UserSignUpResponseDto response = userService.changeNickname(tempToken);
        return ResponseEntity.ok(response);
    }

}
