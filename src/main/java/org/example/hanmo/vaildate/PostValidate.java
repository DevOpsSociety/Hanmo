package org.example.hanmo.vaildate;

import jakarta.servlet.http.HttpServletRequest;

import org.example.hanmo.domain.PostEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.repository.PostRepository;
import org.example.hanmo.repository.UserRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostValidate {

    private final PostRepository postRepository;
    private final RedisTempRepository redisTempRepository;
    private final UserRepository userRepository;

    public UserEntity validateTempToken(HttpServletRequest request) {
        String tempToken = request.getHeader("tempToken");
        if (tempToken == null) {
            throw new NotFoundException("유효하지 않은 토큰입니다.", ErrorCode.INVALID_CODE_EXCEPTION);
        }
        String phoneNumber =
                UserValidate.validatePhoneNumberByTempToken(tempToken, redisTempRepository);
        UserEntity user = UserValidate.getUserByPhoneNumber(phoneNumber, userRepository);
        if (user == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION);
        }
        return user;
    }

    public PostEntity validatePost(Long id) {
        return postRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new NotFoundException(
                                        "게시글을 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));
    }
}
