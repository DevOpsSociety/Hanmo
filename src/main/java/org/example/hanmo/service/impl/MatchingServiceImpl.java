package org.example.hanmo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.example.hanmo.domain.MatchingGroupsEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.Gender;
import org.example.hanmo.domain.enums.GroupStatus;
import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.domain.enums.UserStatus;
import org.example.hanmo.dto.matching.request.OneToOneMatchingRequest;
import org.example.hanmo.dto.matching.request.TwoToTwoMatchingRequest;
import org.example.hanmo.dto.matching.response.MatchingResponse;
import org.example.hanmo.dto.matching.response.MatchingUserInfo;
import org.example.hanmo.dto.user.response.UserProfileResponseDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.MatchingException;
import org.example.hanmo.redis.RedisWaitingRepository;
import org.example.hanmo.repository.MatchingGroupRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.MatchingService;
import org.example.hanmo.vaildate.AuthValidate;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

// 대기 유저를 Redis에 저장 (userStatus: "PENDING" => 매칭 대기 API 호출 시, 유저 대기 상태!)
// Redis에서 대기 유저 수가 차면 DB로 이동 => 대기 유저 수가 조건을 충족하면, Redis에서 대기 유저를 꺼내와서 DB에 매칭 그룹을 저장
// => 이때 DB에는 groupStatus: "MATCHED"로만 저장하게 됨!
// => Redis에서 대기 유저를 직접 조회하여 리스트로 꺼내오는 방식
// 매칭 API 호출 후 유저 상태 업데이트
// 매칭 API를 호출하여 조건에 맞는 유저를 매칭하고, userStatus, groupStatus "MATCHED"로 변경
// 매칭 완료된 유저는 Redis에서 제거하고, DB에서도 제거

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {
    private final RedisWaitingRepository redisWaitingRepository;
    private final MatchingGroupRepository matchingGroupRepository;
    private final UserRepository userRepository;
    private final AuthValidate authValidate;

    // 대기 유저 Redis에 추가, 유저 정보 저장, userStatus "PENDING"
    @Transactional
    public void waitingOneToOneMatching(OneToOneMatchingRequest request) {
        UserEntity user = request.toUserEntity();
        redisWaitingRepository.addUserToWaitingGroupInRedis(request.getGroupId(), user, MatchingType.ONE_TO_ONE);
        user.setUserStatus(UserStatus.PENDING);
    }

    @Transactional
    public void waitingTwoToTwoMatching(TwoToTwoMatchingRequest request) {
        UserEntity user = request.toUserEntity();
        redisWaitingRepository.addUserToWaitingGroupInRedis(request.getGroupId(), user, MatchingType.TWO_TO_TWO);
        user.setUserStatus(UserStatus.PENDING);
    }

    // 1:1 매칭
    @Transactional
    public MatchingResponse matchSameGenderOneToOne(OneToOneMatchingRequest request) {
        List<UserEntity> waitingUsers = redisWaitingRepository.getWaitingUser(request.getGroupId());
        List<UserEntity> maleUsers = filterUsersByGender(waitingUsers, Gender.M);
        List<UserEntity> femaleUsers = filterUsersByGender(waitingUsers, Gender.F);

        // 남성 유저 매칭
        if (maleUsers.size() >= 2) {
            return createOneToOneMatchingGroup(maleUsers);
        }

        // 여성 유저 매칭
        if (femaleUsers.size() >= 2) {
            return createOneToOneMatchingGroup(femaleUsers);
        }

        throw new MatchingException(
                "400_Error, 매칭할 유저 수가 충분하지 않습니다.",
                ErrorCode.INSUFFICIENT_USERS_FOR_MATCHING_EXCEPTION);
    }

    // 2:2 매칭
    @Transactional
    public MatchingResponse matchOppositeGenderTwoToTwo(TwoToTwoMatchingRequest request) {
        List<UserEntity> waitingUsers = redisWaitingRepository.getWaitingUser(request.getGroupId());

        if (waitingUsers.size() >= 4) {
            return createTwoToTwoMatchingGroup(waitingUsers);
        }

        throw new MatchingException(
                "400_Error, 매칭할 유저 수가 충분하지 않습니다.",
                ErrorCode.INSUFFICIENT_USERS_FOR_MATCHING_EXCEPTION);
    }

    // 1:1 매칭 그룹 생성
    @NotNull
    public MatchingResponse createOneToOneMatchingGroup(List<UserEntity> users) {
        // GroupId
        MatchingGroupsEntity matchingGroup =
                MatchingGroupsEntity.builder()
                        .maleCount(
                                (int) users.stream().filter(u -> u.getGender() == Gender.M).count())
                        .femaleCount(
                                (int) users.stream().filter(u -> u.getGender() == Gender.F).count())
                        .isSameDepartment(false) // 학과 상관 없음! 근데 다른 학과가 낫지 않을까?!
                        .groupStatus(GroupStatus.MATCHED)
                        .matchingType(MatchingType.ONE_TO_ONE)
                        .build();

        matchingGroup.addUser(users.get(0));
        matchingGroup.addUser(users.get(1));
        matchingGroupRepository.save(matchingGroup);

        // 매칭 완료된 유저 userStatus "MATCHED", Redis에서 제거
        users.forEach(
                u -> {
                    u.setUserStatus(UserStatus.MATCHED);
                    //
                    redisWaitingRepository.removeUserFromWaitingGroup(matchingGroup.getMatchingGroupId(), u);
                    userRepository.save(u);
                });

        return createOneToOneMatchingResponse(users);
    }

    // 2:2 매칭 그룹 생성
    @NotNull
    public MatchingResponse createTwoToTwoMatchingGroup(List<UserEntity> users) {
        // 동성 유저 그룹 (학과 상관 X)
        List<UserEntity> maleUsers = users.stream().filter(u -> u.getGender() == Gender.M).toList();

        List<UserEntity> femaleUsers =
                users.stream().filter(u -> u.getGender() == Gender.F).toList();

        // 인원 수 확인
        if (maleUsers.size() != 2 || femaleUsers.size() != 2) {
            throw new MatchingException(
                    "400_Error, 매칭할 유저 수가 충분하지 않습니다.",
                    ErrorCode.INSUFFICIENT_USERS_FOR_MATCHING_EXCEPTION);
        }

        // 학과 중복 체크
        if (checkDepartmentConflict(maleUsers, femaleUsers)) {
            throw new MatchingException(
                    "400_Error, 이성 유저 간 학과가 겹칠 수 없습니다.", ErrorCode.DEPARTMENT_CONFLICT_EXCEPTION);
        }

        MatchingGroupsEntity matchingGroup =
                MatchingGroupsEntity.builder()
                        .maleCount(2)
                        .femaleCount(2)
                        .isSameDepartment(false) // 다른 학과!
                        .groupStatus(GroupStatus.MATCHED)
                        .matchingType(MatchingType.TWO_TO_TWO)
                        .build();

        matchingGroup.addUser(maleUsers.get(0));
        matchingGroup.addUser(maleUsers.get(1));
        matchingGroup.addUser(femaleUsers.get(0));
        matchingGroup.addUser(femaleUsers.get(1));
        matchingGroupRepository.save(matchingGroup);

        // 유저 상태 업데이트 및 Redis에서 제거
        users.forEach(
                u -> {
                    u.setUserStatus(UserStatus.MATCHED);
                    //
                    redisWaitingRepository.removeUserFromWaitingGroup(matchingGroup.getMatchingGroupId(), u);
                    userRepository.save(u);
                });

        return createTwoToTwoMatchingResponse(users);
    }

    // 1:1 매칭 응답
    @NotNull
    private MatchingResponse createOneToOneMatchingResponse(List<UserEntity> users) {
        List<MatchingUserInfo> matchedUsers =
                users.stream()
                        .map(
                                user ->
                                        new MatchingUserInfo(
                                                user.getNickname(), user.getInstagramId()))
                        .toList();

        return new MatchingResponse(matchedUsers, MatchingType.ONE_TO_ONE);
    }

    // 2:2 매칭 응답
    @NotNull
    private MatchingResponse createTwoToTwoMatchingResponse(List<UserEntity> users) {
        List<MatchingUserInfo> matchedUsers =
                users.stream()
                        .map(
                                user ->
                                        new MatchingUserInfo(
                                                user.getNickname(), user.getInstagramId()))
                        .toList();

        return new MatchingResponse(matchedUsers, MatchingType.TWO_TO_TWO);
    }

    // 유저 성별 필터링
    public List<UserEntity> filterUsersByGender(List<UserEntity> users, Gender gender) {
        return users.stream().filter(u -> u.getGender() == gender).toList();
    }

    // 매칭 결과 조회
    public List<UserProfileResponseDto> getMatchingResult(String tempToken) {
        UserEntity user = authValidate.validateTempToken(tempToken);
        MatchingGroupsEntity matchingGroup = user.getMatchingGroup();

        if (matchingGroup == null) {
            throw new MatchingException(
                    "404_Error, 매칭 결과가 존재하지 않습니다.", ErrorCode.MATCHING_NOT_FOUND_EXCEPTION);
        }

        return matchingGroup.getUsers().stream()
                .map(
                        matchedUser ->
                                new UserProfileResponseDto(
                                        matchedUser.getNickname(),
                                        matchedUser.getName(),
                                        matchedUser.getInstagramId()))
                .collect(Collectors.toList());
    }

    // 학과 겹치는지 확인
    private boolean checkDepartmentConflict(
            List<UserEntity> maleUsers, List<UserEntity> femaleUsers) {
        return !maleUsers.get(0).getDepartment().equals(femaleUsers.get(0).getDepartment())
                && !maleUsers.get(0).getDepartment().equals(femaleUsers.get(1).getDepartment())
                && !maleUsers.get(1).getDepartment().equals(femaleUsers.get(0).getDepartment())
                && !maleUsers.get(1).getDepartment().equals(femaleUsers.get(1).getDepartment());
    }
}
