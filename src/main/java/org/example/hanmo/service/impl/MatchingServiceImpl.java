package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.MatchingGroupsEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.Gender;
import org.example.hanmo.domain.enums.UserStatus;
import org.example.hanmo.dto.matching.request.OneToOneMatchingRequest;
import org.example.hanmo.dto.matching.request.TwoToTwoMatchingRequest;
import org.example.hanmo.dto.matching.response.MatchingGroupResponse;
import org.example.hanmo.dto.matching.response.MatchingResponse;
import org.example.hanmo.dto.matching.response.MatchingUserInfo;
import org.example.hanmo.redis.RedisWaitingRepository;
import org.example.hanmo.repository.MatchingGroupCustomRepository;
import org.example.hanmo.repository.MatchingGroupRepository;
import org.example.hanmo.service.MatchingService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

// 서비스 로직
// 매칭 대기 API 호출 => 대기 유저를 Redis에 추가
// Redis에서 DB로 넘겨주어야 할 것 같은데??, groupStatus "매칭 대기"로 set
// DB에서 "매칭 대기"인 매칭 그룹 꺼내옴 (query dsl)
// 매칭 API 호출 => 각각 조건에 맞춰 매칭, userStatus를 "MATCHED"로 변경 (groupStatus도 변경이 필요한가? 매칭이 끝나고 매칭 그룹은 어떻게 해야 하지)
// Redis에서 지움 (DB에서 지우는 건가?)

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {
    private final RedisWaitingRepository redisWaitingRepository;
    private final MatchingGroupRepository matchingGroupRepository;
    private final MatchingGroupCustomRepository matchingGroupCustomRepository;

    // 대기 유저 Redis에 추가
    @Override
    public void waitingOneToOneMatching(OneToOneMatchingRequest request, UserEntity user) {
        redisWaitingRepository.addUserToWaitingGroup(request.getUserId(), user);
        user.setUserStatus(UserStatus.PENDING);
        // GroupStatus "매칭 중"으로 변경 <- 근데 이거 왜 enum으로 안 쓰지?
    }

    @Override
    public void waitingTwoToTwoMatching(TwoToTwoMatchingRequest request, UserEntity user) {
        redisWaitingRepository.addUserToWaitingGroup(request.getUserId(), user);
        user.setUserStatus(UserStatus.PENDING);
    }

    // 유저를 매칭 그룹에 추가
    private void addUserToWaitingGroup(UserEntity user, MatchingGroupsEntity matchingGroupsEntity) {
        matchingGroupsEntity.addUser(user);
        matchingGroupsEntity.setGroupStatus("매칭 중");
        matchingGroupRepository.save(matchingGroupsEntity);
    }

    // 매칭 완료 처리
    private void completeMatching(MatchingGroupsEntity matchingGroupsEntity) {
        matchingGroupsEntity.setGroupStatus("매칭 완료");
        matchingGroupsEntity.getUsers().forEach(user -> user.setUserStatus(UserStatus.MATCHED));
        matchingGroupRepository.save(matchingGroupsEntity); // 변경된 매칭 그룹 저장
    }

    // 1:1 매칭
    @Override
    public MatchingResponse matchSameGenderOneToOne(OneToOneMatchingRequest request) {
        List<UserEntity> waitingUsers = redisWaitingRepository.getWaitingUser(request.getUserId());

        List<UserEntity> maleUsers = filterUsersByGender(waitingUsers, Gender.M);
        List<UserEntity> femaleUsers = filterUsersByGender(waitingUsers, Gender.F);

        // 매칭 그룹 조회
        List<MatchingGroupResponse> matchingGroupResponses = matchingGroupCustomRepository.findBySameGenderMatching();


        // 남성 유저 매칭
        if (maleUsers.size() >= 2) {
           return createOneToOneMatchingGroup(maleUsers, request, 2, 0, matchingGroupResponses);
        }

        // 여성 유저 매칭
        else if (femaleUsers.size() >= 2) {
            return createOneToOneMatchingGroup(femaleUsers, request, 0, 2, matchingGroupResponses);
        }

        return null;
    }

    // 2:2 매칭
    @Override
    public MatchingResponse matchOppositeGenderTwoToTwo(TwoToTwoMatchingRequest request) {
        List<UserEntity> waitingUsers = redisWaitingRepository.getWaitingUser(request.getUserId());

        List<MatchingGroupResponse> matchingGroupResponses = matchingGroupCustomRepository.findByOppositeGenderMatching();

        if (waitingUsers.size() >= 4) {
            return createTwoToTwoMatchingGroup(waitingUsers.subList(0, 4), request);
        }

        return null;
    }

    // 유저 성별 필터링
    private List<UserEntity> filterUsersByGender(List<UserEntity> users, Gender gender) {
        return users.stream().filter(u -> u.getGender() == gender).toList();
    }

    // 1:1 매칭 그룹 생성
    @NotNull
    private MatchingResponse createOneToOneMatchingGroup(List<UserEntity> users, OneToOneMatchingRequest request, Integer maleCount, Integer femaleCount,List<MatchingGroupResponse> matchingGroupResponses) {
        UserEntity firstUser = users.get(0);
        UserEntity secondUser = users.get(1);

        MatchingGroupsEntity matchingGroup = MatchingGroupsEntity.builder()
                .maleCount(maleCount)
                .femaleCount(femaleCount)
                .isSameDepartment(true)
                .groupStatus("매칭 완료")
                .users(List.of(firstUser, secondUser))
                .build();

        completeMatching(matchingGroup);

        return getMatchingResponse(request, firstUser, secondUser, matchingGroup);
    }

    // 2:2 매칭 그룹 생성
    private MatchingResponse createTwoToTwoMatchingGroup(List<UserEntity> users, TwoToTwoMatchingRequest request) {
        MatchingGroupsEntity matchingGroup = MatchingGroupsEntity.builder()
                .femaleCount((int) users.stream().filter(u -> u.getGender() == Gender.F).count())
                .maleCount((int) users.stream().filter(u -> u.getGender() == Gender.M).count())
                .isSameDepartment(false)
                .groupStatus("매칭 완료")
                .users(users)
                .build();

        completeMatching(matchingGroup);

        return new MatchingResponse();
    }

    @NotNull
    private MatchingResponse getMatchingResponse(OneToOneMatchingRequest request, UserEntity firstUser, UserEntity secondUser, MatchingGroupsEntity matchingGroup) {
        matchingGroupRepository.save(matchingGroup);

        redisWaitingRepository.removeUserFromWaitingGroup(request.getUserId(), firstUser);
        redisWaitingRepository.removeUserFromWaitingGroup(request.getUserId(), secondUser);

        MatchingResponse response = new MatchingResponse();
        response.setMatchedUsers(List.of(
                new MatchingUserInfo(firstUser.getNickname(), firstUser.getInstagramId()),
                new MatchingUserInfo(secondUser.getNickname(), secondUser.getInstagramId())
        ));

        return response;
    }
}
