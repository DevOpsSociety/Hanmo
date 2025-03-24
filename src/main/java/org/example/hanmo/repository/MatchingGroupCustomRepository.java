package org.example.hanmo.repository;

import org.example.hanmo.dto.matching.response.MatchingGroupResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchingGroupCustomRepository {
    List<MatchingGroupResponse> findBySameGenderMatching();
    List<MatchingGroupResponse> findByOppositeGenderMatching();
}
