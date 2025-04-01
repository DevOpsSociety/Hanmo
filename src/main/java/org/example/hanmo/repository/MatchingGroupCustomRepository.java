package org.example.hanmo.repository;

import java.util.List;

import org.example.hanmo.dto.matching.response.MatchingGroupResponse;

public interface MatchingGroupCustomRepository {
    List<MatchingGroupResponse> findBySameGenderMatching();

    List<MatchingGroupResponse> findByOppositeGenderMatching();
}
