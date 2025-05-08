package org.example.hanmo.repository.post;

import static org.example.hanmo.domain.QPostEntity.postEntity;

import java.util.List;
import java.util.Optional;

import org.example.hanmo.domain.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<PostEntity> getLatestPosts(Pageable pageable) {
    List<PostEntity> results =
        queryFactory
            .selectFrom(postEntity)
            .orderBy(postEntity.createDate.desc()) // 최신순 정렬
            .offset(pageable.getOffset()) // 페이지의 시작 위치
            .limit(pageable.getPageSize()) // 한 페이지당 개수
            .fetch();

    // 전체 데이터 개수 조회 (첫 페이지에서만 실행)
    Long total =
        (pageable.isPaged() && pageable.getPageNumber() == 0)
            ? Optional.ofNullable(
                    queryFactory.select(postEntity.count()).from(postEntity).fetchOne())
                .orElse(0L)
            : null;

    // total이 null이면 현재 데이터 개수로 대체
    return new PageImpl<>(results, pageable, (total != null) ? total : (long) results.size());
  }

  @Override
  public List<PostEntity> findOldPostsToDelete(long excessCount) {
    return queryFactory
        .selectFrom(postEntity)
        .orderBy(postEntity.createDate.asc())
        .limit(excessCount)
        .fetch();
  }

}
