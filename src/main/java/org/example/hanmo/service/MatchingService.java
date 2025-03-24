package org.example.hanmo.service;

import org.example.hanmo.domain.MatchingGroupsEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.matching.request.OneToOneMatchingRequest;
import org.example.hanmo.dto.matching.request.TwoToTwoMatchingRequest;
import org.example.hanmo.dto.matching.response.MatchingResponse;

public interface MatchingService {

    // 매칭 대기
    public void waitingOneToOneMatching(OneToOneMatchingRequest request, UserEntity user);
    public void waitingTwoToTwoMatching(TwoToTwoMatchingRequest request, UserEntity user);


    // 1:1 동성 매칭
    public MatchingResponse matchSameGenderOneToOne(OneToOneMatchingRequest request);

    // 2:2 이성 매칭
    public MatchingResponse matchOppositeGenderTwoToTwo(TwoToTwoMatchingRequest request);

}
