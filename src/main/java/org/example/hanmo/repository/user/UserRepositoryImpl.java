package org.example.hanmo.repository.user;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.QUserEntity;
import org.example.hanmo.dto.admin.response.AdminUserResponseDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.BadRequestException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private static final long FIXED_LIMIT = 30;

    @Override
    public List<AdminUserResponseDto> searchUsersByNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new BadRequestException("닉네임을 입력해주세요.", ErrorCode.BAD_REQUEST_EXCEPTION);
        }

        String kw = nickname.trim();
        QUserEntity u = QUserEntity.userEntity;

        return queryFactory
                .select(Projections.constructor(
                        AdminUserResponseDto.class,
                        u.id,
                        u.nickname,
                        u.name,
                        u.phoneNumber,
                        u.instagramId,
                        u.userRole.stringValue()
                ))
                .from(u)
                .where(u.nickname.containsIgnoreCase(kw))
                .orderBy(u.id.desc())
                .limit(FIXED_LIMIT)
                .fetch();
    }
}
