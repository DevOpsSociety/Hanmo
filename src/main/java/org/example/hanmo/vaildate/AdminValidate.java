package org.example.hanmo.vaildate;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.UserRole;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.ForbiddenException;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminValidate {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthValidate authValidate;
    public UserEntity validateAdminLogin(String phoneNumber,String loginId,String loginPw){
        UserEntity admin = userRepository.findByPhoneNumberAndLoginId(phoneNumber, loginId).orElseThrow(() ->
                        new NotFoundException("관리자 정보를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));

//        // 2) Role 검사
//        if (admin.getUserRole() != UserRole.ADMIN) {
//            throw new ForbiddenException("관리자 권한이 없습니다.", ErrorCode.FORBIDDEN_EXCEPTION);
//        }

        // 3) 비밀번호 검증
        if (!passwordEncoder.matches(loginPw, admin.getLoginPw())) {
            throw new NotFoundException("비밀번호가 일치하지 않습니다.", ErrorCode.INVALID_PASSWORD_EXCEPTION);
        }

        return admin;
    }

    public void verifyAdmin(String tempToken) {
        UserEntity admin = authValidate.validateTempToken(tempToken);
        if (admin.getUserRole() != UserRole.ADMIN) {
            throw new ForbiddenException("관리자 권한이 없습니다.", ErrorCode.FORBIDDEN_EXCEPTION);
        }
    }
}
