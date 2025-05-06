package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.UserRole;
import org.example.hanmo.dto.admin.request.AdminRequestDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.BadRequestException;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.AdminService;
import org.example.hanmo.vaildate.AdminValidate;
import org.example.hanmo.vaildate.UserValidate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {
    private final AdminValidate adminValidate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTempRepository redisTempRepository;

    @Override
    public String loginAdmin(AdminRequestDto dto) {
        UserEntity admin=adminValidate.validateAdminLogin(dto.getPhoneNumber(), dto.getLoginId(), dto.getLoginPw());

        return redisTempRepository.createTempTokenForUser(admin.getPhoneNumber(), true);
        // 이 부분 수정해야합니다. 원래 어드민 로그인이 맞고 성공한다면
        //토큰값 bearer값을 넘겨주어야하고, 지금은 구현이 안되어 임시토큰을 넘겨줍니다.
        //나중에 토큰 완성되면 변경합니다. 테스트를 위해 임시토큰을 넣어놓습니다.
    }

    @Override
    public void addAdminInfo(AdminRequestDto dto) {
        UserEntity user = UserValidate.getUserByPhoneNumber(dto.getPhoneNumber(), userRepository);
        // 2) Role 확인
        if (user.getUserRole() != UserRole.ADMIN) {
            throw new BadRequestException("관리자 승격된 계정이 아닙니다.", ErrorCode.FORBIDDEN_EXCEPTION);
        }
        // 3) 이미 정보가 있으면 중복 에러
        if (StringUtils.isNotBlank(user.getLoginId()) || StringUtils.isNotBlank(user.getLoginPw())) {
            throw new BadRequestException("이미 관리자 추가정보가 등록되어 있습니다.", ErrorCode.DUPLICATE_ACCOUNT_EXCEPTION);
        }
        // 4) ID/PW 설정
        if (userRepository.existsByLoginId(dto.getLoginId())) {
            throw new BadRequestException("이미 사용 중인 로그인 아이디입니다.", ErrorCode.DUPLICATE_ACCOUNT_EXCEPTION);
        }
        user.setLoginId(dto.getLoginId());
        user.setLoginPw(passwordEncoder.encode(dto.getLoginPw()));
        // 5) 저장
        userRepository.save(user);
    }
}
