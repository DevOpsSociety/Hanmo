package org.example.hanmo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.example.hanmo.domain.QMatchingGroupsEntity;
import org.example.hanmo.dto.matching.response.MatchingGroupResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class MatchingGroupCustomRepositoryImpl implements MatchingGroupCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    // Db에서 groupStatus "매칭 대기" 상태인 매칭 그룹을 조회
    @Override
    public List<MatchingGroupResponse> findBySameGenderMatching() {
        QMatchingGroupsEntity qMatchingGroupsEntity = QMatchingGroupsEntity.matchingGroupsEntity;

        // MatchingGroupsEntity에서 필드 선택, 새로운 MatchingGroupResponse 객체 생성
        return jpaQueryFactory.select(Projections.constructor(MatchingGroupResponse.class,
                        qMatchingGroupsEntity.matchingGroupId,
                        qMatchingGroupsEntity.maleCount,
                        qMatchingGroupsEntity.femaleCount,
                        qMatchingGroupsEntity.groupStatus))
                .from(qMatchingGroupsEntity) // MatchingGroupsEntity로부터 데이터 가져오기
                .where(qMatchingGroupsEntity.groupStatus.eq("매칭 대기")
                        .and(qMatchingGroupsEntity.maleCount.eq(2)
                            .or(qMatchingGroupsEntity.femaleCount.eq(2))))
                .fetch();
    }

    @Override
    public List<MatchingGroupResponse> findByOppositeGenderMatching() {
        QMatchingGroupsEntity qMatchingGroupsEntity = QMatchingGroupsEntity.matchingGroupsEntity;

        return jpaQueryFactory.select(Projections.constructor(MatchingGroupResponse.class,
                        qMatchingGroupsEntity.matchingGroupId,
                        qMatchingGroupsEntity.maleCount,
                        qMatchingGroupsEntity.femaleCount,
                        qMatchingGroupsEntity.groupStatus))
                .from(qMatchingGroupsEntity)
                .where(qMatchingGroupsEntity.groupStatus.eq("매칭 대기")
                        .and(qMatchingGroupsEntity.maleCount.eq(2))
                        .and(qMatchingGroupsEntity.femaleCount.eq(2)))
                .fetch();
    }



}
