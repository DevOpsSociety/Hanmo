package org.example.hanmo.repository.NmoApply;

import org.example.hanmo.domain.NmoApplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NmoApplyRepository extends JpaRepository<NmoApplyEntity, Long>, NmoApplyRepositoryCustom {
  boolean existsByUserIdAndNmoId(Long userId, Long nmoId);
  Optional<NmoApplyEntity> findByUserIdAndNmoId(Long userId, Long nmoId);
  List<NmoApplyEntity> findAllByNmoId(Long nmoId);
  int countByNmoId(Long nmoId); // 추가
}

