package org.example.hanmo.repository;

import java.util.List;

import org.example.hanmo.domain.MatchingGroupsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingGroupRepository
        extends JpaRepository<MatchingGroupsEntity, Long>, MatchingGroupCustomRepository {
    List<MatchingGroupsEntity> findByGroupId(Long groupId);
}
