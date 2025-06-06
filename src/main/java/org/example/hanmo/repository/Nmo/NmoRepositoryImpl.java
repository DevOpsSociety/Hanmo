package org.example.hanmo.repository.Nmo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.NmoEntity;
import org.example.hanmo.domain.QNmoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NmoRepositoryImpl implements NmoRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<NmoEntity> findNmoListAfterId(Long lastId, int size) {
    QNmoEntity nmo = QNmoEntity.nmoEntity;

    return queryFactory
        .selectFrom(nmo)
        .where(
            lastId != null ? nmo.id.lt(lastId) : null // lt: less than → 최신순
        )
        .orderBy(nmo.id.desc())
        .limit(size)
        .fetch();
  }

  @Override
  public List<NmoEntity> findByAuthorId(Long authorId, Long lastId, int size) {
    QNmoEntity nmo = QNmoEntity.nmoEntity;

    return queryFactory
        .selectFrom(nmo)
        .where(
            nmo.author.id.eq(authorId),
            lastId != null ? nmo.id.lt(lastId) : null // 최신순 페이징
        )
        .orderBy(nmo.id.desc())
        .limit(size)
        .fetch();
  }
}
