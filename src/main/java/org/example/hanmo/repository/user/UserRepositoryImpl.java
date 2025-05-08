package org.example.hanmo.repository.user;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.hanmo.domain.QUserEntity;
import org.example.hanmo.dto.admin.response.AdminUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    @Override
    public Page<AdminUserResponseDto> searchUsersByKeyword(String keyword, Pageable pageable) {
        QUserEntity u = QUserEntity.userEntity;

        JPAQuery<AdminUserResponseDto> query = queryFactory
                .select(Projections.constructor(
                        AdminUserResponseDto.class,
                        u.id,
                        u.studentNumber,
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
                .where(
                        StringUtils.isNotBlank(keyword)
                                ? u.nickname.containsIgnoreCase(keyword)
                                .or(u.name.containsIgnoreCase(keyword))
                                : null
                )
                .orderBy(u.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<AdminUserResponseDto> content = query.fetch();
        long total = query.fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}
