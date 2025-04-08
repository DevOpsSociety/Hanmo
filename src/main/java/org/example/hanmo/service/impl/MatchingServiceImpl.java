package org.example.hanmo.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.example.hanmo.domain.MatchingGroupsEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.*;
import org.example.hanmo.dto.matching.request.RedisUserDto;
import org.example.hanmo.dto.matching.response.MatchingResponse;
import org.example.hanmo.dto.matching.response.MatchingUserInfo;
import org.example.hanmo.dto.user.response.UserProfileResponseDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.MatchingException;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.redis.RedisWaitingRepository;
import org.example.hanmo.repository.MatchingGroupRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.MatchingService;
import org.example.hanmo.vaildate.AuthValidate;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 대기 유저를 Redis에 저장 (userStatus: "PENDING" => 매칭 대기 API 호출 시, 유저 대기 상태!)
// Redis에서 대기 유저 수가 차면 DB로 이동 => 대기 유저 수가 조건을 충족하면, Redis에서 대기 유저를 꺼내와서 DB에 매칭 그룹을 저장
// => 이때 DB에는 groupStatus: "MATCHED"로만 저장하게 됨!
// => Redis에서 대기 유저를 직접 조회하여 리스트로 꺼내오는 방식
// 매칭 API 호출 후 유저 상태 업데이트
// 매칭 API를 호출하여 조건에 맞는 유저를 매칭하고, userStatus, groupStatus "MATCHED"로 변경
// 매칭 완료된 유저는 Redis에서 제거하고, DB에서도 제거

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingServiceImpl implements MatchingService {
    private final RedisWaitingRepository redisWaitingRepository;
    private final MatchingGroupRepository matchingGroupRepository;
    private final UserRepository userRepository;
    private final AuthValidate authValidate;

    // 대기 유저 Redis에 추가, 유저 정보 저장, userStatus "PENDING"
    @Transactional
    public void waitingOneToOneMatching(RedisUserDto userDto) {
        UserEntity user =
                userRepository
                        .findById(userDto.getId())
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "404_Error, 유저를 찾을 수 없습니다.",
                                                ErrorCode.NOT_FOUND_EXCEPTION));

        user.setUserStatus(UserStatus.PENDING);
        user.setMatchingType(MatchingType.ONE_TO_ONE);
        userRepository.save(user);

        userDto.setUserStatus(UserStatus.PENDING);
        redisWaitingRepository.addUserToWaitingGroupInRedis(userDto, MatchingType.ONE_TO_ONE);
    }

    @Transactional
    public void waitingTwoToTwoMatching(RedisUserDto userDto) {
        UserEntity user =
                userRepository
                        .findById(userDto.getId())
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "404_Error, 유저를 찾을 수 없습니다.",
                                                ErrorCode.NOT_FOUND_EXCEPTION));

        user.setUserStatus(UserStatus.PENDING);
        user.setMatchingType(MatchingType.TWO_TO_TWO);
        userRepository.save(user);

        userDto.setUserStatus(UserStatus.PENDING);
        redisWaitingRepository.addUserToWaitingGroupInRedis(userDto, MatchingType.TWO_TO_TWO);
    }

    // 1:1 매칭
    @Transactional
    public MatchingResponse matchSameGenderOneToOne(String tempToken) {
        UserEntity user = authValidate.validateTempToken(tempToken);
        Gender currentGender = user.getGender();

        List<RedisUserDto> waitingUserDto =
                redisWaitingRepository.getWaitingUsers(MatchingType.ONE_TO_ONE);

        // 자기 자신 제외, 다른 학과, 같은 성별, 상태 PENDING만
        List<RedisUserDto> filteredUsers =
                waitingUserDto.stream()
                        .filter(u -> u.getUserStatus() == UserStatus.PENDING)
                        .filter(u -> !u.getId().equals(user.getId()))
                        .filter(u -> u.getGender() == currentGender) // 같은 성별
                        .filter(u -> !u.getDepartment().equals(user.getDepartment())) // 다른 학과
                        .toList();

        if (filteredUsers.isEmpty()) {
            throw new NotFoundException(
                    "404_Error, 매칭 가능한 유저가 존재하지 않습니다.", ErrorCode.NO_MATCHING_PARTNER_EXCEPTION);
        }

        // 랜덤으로 한 명 선택
        RedisUserDto matchedUserDto =
                filteredUsers.get(ThreadLocalRandom.current().nextInt(filteredUsers.size()));

        // 매칭 대상 조회 (상태가 PENDING인 유저만)
        UserEntity matchedUser =
                userRepository
                        .findById(matchedUserDto.getId())
                        .orElseThrow(
                                () ->
                                        new MatchingException(
                                                "404_Error, 매칭 대기열에 존재하는 유저 정보를 DB에서 찾을 수 없습니다.",
                                                ErrorCode.MATCHING_NOT_FOUND_EXCEPTION));

        if (matchedUser.getUserStatus() != UserStatus.PENDING) {
            throw new MatchingException("409_Error, 이미 매칭된 유저입니다.", ErrorCode.USER_ALREADY_MATCHED);
        }

        // Redis 대기열에서 제거
        redisWaitingRepository.removeUserFromWaitingGroup(
                MatchingType.ONE_TO_ONE, List.of(user.toRedisUserDto(), matchedUserDto));

        return createOneToOneMatchingGroup(List.of(user, matchedUser));
    }

    // 2:2 매칭
    /*
    @Transactional
    public MatchingResponse matchOppositeGenderTwoToTwo(String tempToken) {
        UserEntity user = authValidate.validateTempToken(tempToken);
        List<RedisUserDto> waitingUserDto =
                redisWaitingRepository.getWaitingUsers(MatchingType.TWO_TO_TWO);

        List<RedisUserDto> filteredUsers =
                waitingUserDto.stream()
                        .filter(u -> u.getUserStatus() == UserStatus.PENDING)
                        .filter(u -> !u.getId().equals(user.getId()))
                        .filter(
                                u -> {
                                    // 이성은 다른 학과, 동성은 학과 상관 없음
                                    if (!u.getGender().equals(user.getGender())) {
                                        return !u.getDepartment().equals(user.getDepartment());
                                    }
                                    return true;
                                })
                        .toList();

        if (filteredUsers.size() < 3) {
            throw new NotFoundException(
                    "404_Error, 매칭할 유저 수가 충분하지 않습니다.", ErrorCode.NO_MATCHING_PARTNER_EXCEPTION);
        }

        List<RedisUserDto> matchedDtos = new ArrayList<>(filteredUsers.subList(0, 3));
        matchedDtos.add(user.toRedisUserDto());

        // Redis에서 대기열 제거
        redisWaitingRepository.removeUserFromWaitingGroup(MatchingType.TWO_TO_TWO, matchedDtos);

        // 실제 DB에서 유저 엔티티 조회 및 상태 확인
        List<UserEntity> matchedUsers =
                matchedDtos.stream()
                        .map(
                                dto -> {
                                    UserEntity matchedUser =
                                            userRepository
                                                    .findById(dto.getId())
                                                    .orElseThrow(
                                                            () ->
                                                                    new NotFoundException(
                                                                            "404_Error, 유저를 찾을 수 없습니다.",
                                                                            ErrorCode
                                                                                    .NOT_FOUND_EXCEPTION));

                                    if (matchedUser.getUserStatus() != UserStatus.PENDING) {
                                        throw new MatchingException(
                                                "409_Error, 이미 매칭된 유저입니다.",
                                                ErrorCode.USER_ALREADY_MATCHED);
                                    }

                                    matchedUser.setUserStatus(UserStatus.MATCHED);
                                    return matchedUser;
                                })
                        .toList();

        return createTwoToTwoMatchingGroup(matchedUsers);
    }
    */

    @Transactional
    public MatchingResponse matchOppositeGenderTwoToTwo(String tempToken) {
        UserEntity user = authValidate.validateTempToken(tempToken);
        Gender myGender = user.getGender();
        Department myDept = user.getDepartment();

        List<RedisUserDto> waitingUsers =
                redisWaitingRepository.getWaitingUsers(MatchingType.TWO_TO_TWO);

        // 나를 제외하고, PENDING 상태이며 조건에 맞는 사용자 필터링
        List<RedisUserDto> filteredUsers =
                waitingUsers.stream()
                        .filter(u -> u.getUserStatus() == UserStatus.PENDING)
                        .filter(u -> !u.getId().equals(user.getId()))
                        .filter(
                                u -> {
                                    boolean sameGender = u.getGender() == myGender;
                                    boolean sameDept = u.getDepartment().equals(myDept);
                                    return sameGender || (!sameDept); // 동성은 상관 없음, 이성은 학과 다르게
                                })
                        .toList();

        if (filteredUsers.size() < 3) {
            throw new NotFoundException(
                    "404_Error, 매칭할 유저 수가 충분하지 않습니다.", ErrorCode.NO_MATCHING_PARTNER_EXCEPTION);
        }

        List<RedisUserDto> matchedDtos = new ArrayList<>(filteredUsers.subList(0, 3));
        matchedDtos.add(user.toRedisUserDto());

        // Redis 대기열에서 제거
        redisWaitingRepository.removeUserFromWaitingGroup(MatchingType.TWO_TO_TWO, matchedDtos);

        // 실제 DB에서 유저 엔티티 조회 및 상태 검증
        List<UserEntity> matchedUsers =
                matchedDtos.stream()
                        .map(
                                dto -> {
                                    if (dto.getId().equals(user.getId())) {
                                        user.setUserStatus(UserStatus.MATCHED);
                                        return user;
                                    }

                                    UserEntity matchedUser =
                                            userRepository
                                                    .findById(dto.getId())
                                                    .orElseThrow(
                                                            () ->
                                                                    new NotFoundException(
                                                                            "404_Error, 유저를 찾을 수 없습니다.",
                                                                            ErrorCode
                                                                                    .NOT_FOUND_EXCEPTION));

                                    if (matchedUser.getUserStatus() != UserStatus.PENDING) {
                                        throw new MatchingException(
                                                "409_Error, 이미 매칭된 유저입니다.",
                                                ErrorCode.USER_ALREADY_MATCHED);
                                    }

                                    matchedUser.setUserStatus(UserStatus.MATCHED);
                                    return matchedUser;
                                })
                        .toList();

        return createTwoToTwoMatchingGroup(matchedUsers);
    }

    // 1:1 매칭 그룹 생성
    @Transactional
    public MatchingResponse createOneToOneMatchingGroup(List<UserEntity> users) {
        MatchingGroupsEntity matchingGroup =
                MatchingGroupsEntity.builder()
                        .maleCount(
                                (int) users.stream().filter(u -> u.getGender() == Gender.M).count())
                        .femaleCount(
                                (int) users.stream().filter(u -> u.getGender() == Gender.F).count())
                        .isSameDepartment(false) // 다른 학과!
                        .groupStatus(GroupStatus.MATCHED)
                        .matchingType(MatchingType.ONE_TO_ONE)
                        .build();

        matchingGroup.addUser(users.get(0));
        matchingGroup.addUser(users.get(1));
        matchingGroupRepository.save(matchingGroup);

        // 매칭 완료된 유저 userStatus "MATCHED"
        users.forEach(
                u -> {
                    u.setUserStatus(UserStatus.MATCHED);
                    userRepository.save(u);
                });

        return createOneToOneMatchingResponse(users);
    }

    // 2:2 매칭 그룹 생성
    @Transactional
    public MatchingResponse createTwoToTwoMatchingGroup(List<UserEntity> users) {
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

        // 매칭 완료된 유저 userStatus "MATCHED"
        users.forEach(
                u -> {
                    u.setUserStatus(UserStatus.MATCHED);
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
    private List<RedisUserDto> filterUsersByGender(List<RedisUserDto> users, Gender gender) {
        return users.stream().filter(u -> u.getGender().equals(gender)).toList();
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
