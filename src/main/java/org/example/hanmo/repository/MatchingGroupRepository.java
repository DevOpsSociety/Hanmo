package org.example.hanmo.repository;

import org.example.hanmo.domain.MatchingGroupsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingGroupRepository extends JpaRepository<MatchingGroupsEntity, Long> {
  //    List<MatchingGroupsEntity> findByGroupId(Long groupId);
}
