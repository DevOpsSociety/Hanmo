package org.example.hanmo.repository.Nmo;

import org.example.hanmo.domain.NmoEntity;

import java.util.List;

public interface NmoRepositoryCustom {
  List<NmoEntity> findNmoListAfterId(Long lastId, int size);
  List<NmoEntity> findByAuthorId(Long authorId, Long lastId, int size);
}
