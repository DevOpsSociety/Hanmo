package org.example.hanmo.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingServiceImpl implements MatchingService {

    private final RedisWaitingRepository redisWaitingRepository;
    private final MatchingGroupRepository matchingGroupRepository;
    private final UserRepository userRepository;
    private final AuthValidate authValidate;

    // 1:1 매칭 대기: 유저 정보 저장 및 Redis 대기열 등록
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

        // 이미 매칭 그룹에 속해 있다면 이미 매칭이 되어있다는 예외 처리
        if (user.getMatchingGroup() != null) {
            throw new MatchingException("이미 매칭이 되어있습니다.", ErrorCode.NO_MATCHING_PARTNER_EXCEPTION);
        }

        // 이미 PENDING 상태이면 매칭 신청이 진행 중임
        if (user.getUserStatus() == UserStatus.PENDING) {
            throw new MatchingException(
                    "이미 매칭 신청이 진행 중입니다.", ErrorCode.NO_MATCHING_PARTNER_EXCEPTION);
        }

        // 이미 다른 매칭 타입(2:2)으로 신청되어 있다면 예외 처리
        if (user.getMatchingType() != null && user.getMatchingType() == MatchingType.TWO_TO_TWO) {
            throw new MatchingException(
                    "이미 2:2 매칭으로 신청하셔서 1:1 매칭 신청이 불가능합니다.",
                    ErrorCode.NO_MATCHING_PARTNER_EXCEPTION);
        }

        user.setUserStatus(UserStatus.PENDING);
        user.setMatchingType(MatchingType.ONE_TO_ONE);
        userRepository.save(user);

        userDto.setUserStatus(UserStatus.PENDING);
        redisWaitingRepository.addUserToWaitingGroupInRedis(userDto, MatchingType.ONE_TO_ONE);

        log.info("1:1 매칭 신청을 시작하였습니다. Redis에 대기 데이터가 등록되었습니다.");
    }

    // 2:2 매칭 대기: 유저 정보 저장 및 Redis 대기열 등록
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

        // 이미 매칭 그룹에 속해 있다면 이미 매칭이 되어있다는 예외 처리
        if (user.getMatchingGroup() != null) {
            throw new MatchingException("이미 매칭이 되어있습니다.", ErrorCode.NO_MATCHING_PARTNER_EXCEPTION);
        }

        // 이미 매칭 신청이 진행 중이면 예외 처리
        if (user.getUserStatus() == UserStatus.PENDING) {
            throw new MatchingException(
                    "이미 매칭 신청이 진행 중입니다.", ErrorCode.NO_MATCHING_PARTNER_EXCEPTION);
        }

        // 이미 1:1 대기열에 등록되어 있다면 예외 처리
        if (user.getMatchingType() != null && user.getMatchingType() == MatchingType.ONE_TO_ONE) {
            throw new MatchingException(
                    "이미 1:1 매칭으로 신청하셔서 2:2 매칭 신청이 불가능합니다.",
                    ErrorCode.NO_MATCHING_PARTNER_EXCEPTION);
        }

        user.setUserStatus(UserStatus.PENDING);
        user.setMatchingType(MatchingType.TWO_TO_TWO);
        userRepository.save(user);

        userDto.setUserStatus(UserStatus.PENDING);
        redisWaitingRepository.addUserToWaitingGroupInRedis(userDto, MatchingType.TWO_TO_TWO);

        log.info("2:2 매칭 신청을 시작하였습니다. Redis에 대기 데이터가 등록되었습니다.");
    }

    // 1:1 매칭 처리
    @Transactional
    public MatchingResponse matchSameGenderOneToOne(String tempToken) {
        UserEntity user = authValidate.validateTempToken(tempToken);
        Gender myGender = user.getGender();

        List<RedisUserDto> waitingUserDto =
                redisWaitingRepository.getWaitingUsers(MatchingType.ONE_TO_ONE);

        // 조건: 자기 자신 제외, 다른 학과, 같은 성별, 상태 PENDING
        List<RedisUserDto> filteredUsers =
                waitingUserDto.stream()
                        .filter(u -> u.getUserStatus() == UserStatus.PENDING)
                        .filter(u -> !u.getId().equals(user.getId()))
                        .filter(u -> u.getGender() == myGender)
                        .filter(u -> !u.getDepartment().equals(user.getDepartment()))
                        .toList();

        if (filteredUsers.isEmpty()) {
            throw new NotFoundException(
                    "400_Error, 매칭 가능한 유저가 존재하지 않습니다.", ErrorCode.NO_MATCHING_PARTNER_EXCEPTION);
        }

        // 랜덤으로 한 명 선택
        RedisUserDto matchedUserDto =
                filteredUsers.get(ThreadLocalRandom.current().nextInt(filteredUsers.size()));

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

        // 매칭 후, Redis 대기열에서 두 유저 제거
        redisWaitingRepository.removeUserFromWaitingGroup(
                MatchingType.ONE_TO_ONE, List.of(user.toRedisUserDto(), matchedUserDto));

        // 클린업: 매칭 후 대기열이 비어있으면 전체 키 삭제
        if (redisWaitingRepository.getWaitingUsers(MatchingType.ONE_TO_ONE).isEmpty()) {
            redisWaitingRepository.clearWaitingGroup(MatchingType.ONE_TO_ONE);
        }

        log.info("1:1 매칭이 완료되었습니다.");

        return createOneToOneMatchingGroup(List.of(user, matchedUser));
    }

    // 2:2 매칭 처리
    @Transactional
    public MatchingResponse matchOppositeGenderTwoToTwo(String tempToken) {
        UserEntity user = authValidate.validateTempToken(tempToken);
        Gender myGender = user.getGender();
        Department myDept = user.getDepartment();

        List<RedisUserDto> waitingUsers =
                redisWaitingRepository.getWaitingUsers(MatchingType.TWO_TO_TWO);

        List<RedisUserDto> filteredUsers =
                waitingUsers.stream()
                        .filter(u -> u.getUserStatus() == UserStatus.PENDING)
                        .filter(u -> !u.getId().equals(user.getId()))
                        .filter(
                                u -> {
                                    if (u.getGender() == myGender) {
                                        return true;
                                    } else {
                                        return !u.getDepartment().equals(myDept);
                                    }
                                })
                        .toList();

        if (filteredUsers.size() < 3) {
            throw new NotFoundException(
                    "400_Error, 매칭할 유저 수가 충분하지 않습니다.", ErrorCode.NO_MATCHING_PARTNER_EXCEPTION);
        }

        Set<Integer> selectedIndexes = new HashSet<>();
        List<RedisUserDto> matchedDtos = new ArrayList<>();
        matchedDtos.add(user.toRedisUserDto());

        while (matchedDtos.size() < 4) {
            int randomIndex = ThreadLocalRandom.current().nextInt(filteredUsers.size());
            if (!selectedIndexes.contains(randomIndex)) {
                matchedDtos.add(filteredUsers.get(randomIndex));
                selectedIndexes.add(randomIndex);
            }
            if (selectedIndexes.size() == filteredUsers.size()) {
                break;
            }
        }

        redisWaitingRepository.removeUserFromWaitingGroup(MatchingType.TWO_TO_TWO, matchedDtos);

        // 클린업: 매칭 완료 후 대기열이 비어있으면 전체 키 삭제
        if (redisWaitingRepository.getWaitingUsers(MatchingType.TWO_TO_TWO).isEmpty()) {
            redisWaitingRepository.clearWaitingGroup(MatchingType.TWO_TO_TWO);
        }

        List<UserEntity> matchedUsers = new ArrayList<>();
        for (RedisUserDto dto : matchedDtos) {
            UserEntity matchedUser =
                    userRepository
                            .findById(dto.getId())
                            .orElseThrow(
                                    () ->
                                            new NotFoundException(
                                                    "404_Error, 유저를 찾을 수 없습니다.",
                                                    ErrorCode.NOT_FOUND_EXCEPTION));
            if (matchedUser.getUserStatus() != UserStatus.PENDING) {
                throw new MatchingException(
                        "409_Error, 이미 매칭된 유저입니다.", ErrorCode.USER_ALREADY_MATCHED);
            }
            matchedUser.setUserStatus(UserStatus.MATCHED);
            matchedUsers.add(matchedUser);
        }

        user.setUserStatus(UserStatus.MATCHED);
        matchedUsers.add(user);

        log.info("2:2 매칭이 완료되었습니다.");

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
                        .isSameDepartment(false)
                        .groupStatus(GroupStatus.MATCHED)
                        .matchingType(MatchingType.ONE_TO_ONE)
                        .build();

        matchingGroup.addUser(users.get(0));
        matchingGroup.addUser(users.get(1));
        matchingGroupRepository.save(matchingGroup);

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

        if (checkDepartmentConflict(maleUsers, femaleUsers)) {
            throw new MatchingException(
                    "400_Error, 이성 유저 간 학과가 겹칠 수 없습니다.", ErrorCode.DEPARTMENT_CONFLICT_EXCEPTION);
        }

        MatchingGroupsEntity matchingGroup =
                MatchingGroupsEntity.builder()
                        .maleCount(2)
                        .femaleCount(2)
                        .isSameDepartment(false)
                        .groupStatus(GroupStatus.MATCHED)
                        .matchingType(MatchingType.TWO_TO_TWO)
                        .build();

        matchingGroup.addUser(maleUsers.get(0));
        matchingGroup.addUser(maleUsers.get(1));
        matchingGroup.addUser(femaleUsers.get(0));
        matchingGroup.addUser(femaleUsers.get(1));
        matchingGroupRepository.save(matchingGroup);

        users.forEach(
                u -> {
                    u.setUserStatus(UserStatus.MATCHED);
                    userRepository.save(u);
                });

        return createTwoToTwoMatchingResponse(users);
    }

    // 1:1 매칭 응답 생성
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

    // 2:2 매칭 응답 생성
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

    // 학과 중복 여부 체크
    private boolean checkDepartmentConflict(
            List<UserEntity> maleUsers, List<UserEntity> femaleUsers) {
        return maleUsers.stream()
                .anyMatch(
                        male ->
                                femaleUsers.stream()
                                        .anyMatch(
                                                female ->
                                                        male.getDepartment()
                                                                .equals(female.getDepartment())));
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
}
