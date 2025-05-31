package org.example.hanmo.repository.Nmo;

import org.example.hanmo.domain.NmoEntity;

import java.util.List;

public interface NmoRepositoryCustom {
  List<NmoEntity> findNmoListAfterId(Long lastId, int size);
}
