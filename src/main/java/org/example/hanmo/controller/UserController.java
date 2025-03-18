package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.dto.user.response.UserSignUpResponseDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.redis.RedisSmsRepository;
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

    @Operation(summary = "회원가입")
    @PostMapping("/signUp")
    public ResponseEntity<UserSignUpResponseDto> signUp(@Valid @RequestBody UserSignUpRequestDto requestDto) {
        UserSignUpResponseDto responseDto = userService.signUpUser(requestDto);
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "닉네임 변경")
    @PostMapping("/change-nickname")
    public UserEntity changeNickname(@RequestParam("phoneNumber") String phoneNumber) {
        if (!redisSmsRepository.isVerifiedFlag(phoneNumber)) {
            throw new NotFoundException("400_Error, 인증이 되지않습니다.", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
        }

        // 해당 전화번호의 사용자를 조회
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException("404_Error, 유저를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));

        // 중복 없이 새로운 랜덤 닉네임 할당
        UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository);

        // 재생성 후 플래그 삭제 (한 번만 허용)
        redisSmsRepository.deleteVerifiedFlag(phoneNumber);

        return userRepository.save(user);
    }
}
