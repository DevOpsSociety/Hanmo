package org.example.hanmo.repository;

import org.example.hanmo.domain.MatchingGroupsEntity;
import org.example.hanmo.domain.enums.GroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MatchingGroupRepository extends JpaRepository<MatchingGroupsEntity, Long> {
  //    List<MatchingGroupsEntity> findByGroupId(Long groupId);

    long countByGroupStatusAndCreateDateBetween(GroupStatus groupStatus, LocalDateTime fromInclusive, LocalDateTime toExclusive);

    long countByGroupStatus(GroupStatus status);
}
