package org.example.hanmo.repository.NmoApplication;

import org.example.hanmo.domain.NmoApplicationEntity;
import org.example.hanmo.domain.NmoEntity;
import org.example.hanmo.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NmoApplyRepository extends JpaRepository<NmoApplicationEntity, Long>, NmoApplyRepositoryCustom {
  boolean existsByUserIdAndNmoId(Long userId, Long nmoId);
  Optional<NmoApplicationEntity> findByUserIdAndNmoId(Long userId, Long nmoId);
  List<NmoApplicationEntity> findByNmoId(Long nmoId);
  int countByNmoId(Long nmoId); // 추가
}

