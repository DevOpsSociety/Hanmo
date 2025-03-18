package org.example.hanmo.vaildate;

import org.apache.commons.lang3.StringUtils;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.BadRequestException;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.util.RandomNicknameUtil;

public class UserValidate {
    public static void validateDuplicateNickname(String nickname, UserRepository userRepository) {
        if (StringUtils.isNotBlank(nickname) && userRepository.existsByNickname(nickname)) {
            throw new BadRequestException("409_Error, 이미 사용중인 닉네임입니다.", ErrorCode.DUPLICATE_NICKNAME_EXCEPTION);
        }
    }

    public static void setUniqueRandomNicknameIfNeeded(UserEntity user, boolean regenerate, UserRepository userRepository) {
        if (regenerate || user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            String uniqueNickname = java.util.stream.Stream
                    .generate(() -> RandomNicknameUtil.generateNickname(user.getDepartment()))
                    .filter(nickname -> !userRepository.existsByNickname(nickname))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("409",ErrorCode.DUPLICATE_NICKNAME_EXCEPTION));
            user.setNickname(uniqueNickname);
        }
    }
}
