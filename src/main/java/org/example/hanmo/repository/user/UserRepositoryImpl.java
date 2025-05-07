package org.example.hanmo.repository.user;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
        QUserEntity u = QUserEntity.userEntity;

        JPAQuery<AdminUserResponseDto> query = queryFactory
                .select(Projections.constructor(
                        AdminUserResponseDto.class,
                        u.id,
                        u.nickname,
                        u.name,
                        u.phoneNumber,
                        u.instagramId,
                        u.userRole.stringValue(),
                        u.userStatus,
                        u.matchingGroup.matchingGroupId,
                        u.matchingType
                ))
                .from(u)
                .orderBy(u.id.desc())
                .limit(FIXED_LIMIT);

        if (StringUtils.isNotBlank(nickname)) {
            String kw = nickname.trim();
            BooleanExpression predicate = u.nickname.containsIgnoreCase(kw).or(u.name.containsIgnoreCase(kw));
            query.where(predicate);
        }
        return query.fetch();
    }
}
