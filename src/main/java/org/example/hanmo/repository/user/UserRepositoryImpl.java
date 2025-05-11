package org.example.hanmo.repository.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.hanmo.domain.QUserEntity;
import org.example.hanmo.domain.enums.UserStatus;
import org.example.hanmo.dto.admin.response.AdminUserResponseDto;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminUserResponseDto> searchUsersByKeyword(
            String keyword, UserStatus status, Pageable pageable) {

        QUserEntity u = QUserEntity.userEntity;

        BooleanBuilder cond = new BooleanBuilder();
        if (StringUtils.isNotBlank(keyword)) {
            cond.and(u.nickname.containsIgnoreCase(keyword)
                    .or(u.name.containsIgnoreCase(keyword)));
        }
        if (status != null) {
            cond.and(u.userStatus.eq(status));
        }

        List<AdminUserResponseDto> content = queryFactory
                .select(Projections.constructor(
                        AdminUserResponseDto.class,
                        u.id,
                        u.studentNumber,
                        u.nickname,
                        u.name,
                        u.phoneNumber,
                        u.instagramId,
                        u.userRole.stringValue(),
                        u.gender.stringValue(),
                        u.genderMatchingType.stringValue(),
                        u.userStatus,
                        u.matchingGroup.matchingGroupId,
                        u.matchingType
                ))
                .from(u)
                .where(cond)
                .orderBy(u.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(u.count())
                .from(u)
                .where(cond)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
