package org.example.hanmo.repository;

import org.example.hanmo.domain.MatchingGroupsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchingGroupRepository extends JpaRepository<MatchingGroupsEntity, Long>, MatchingGroupCustomRepository {
    List<MatchingGroupsEntity> findByGroupId(Long groupId);

}