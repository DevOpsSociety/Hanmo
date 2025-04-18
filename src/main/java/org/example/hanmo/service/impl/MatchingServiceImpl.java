package org.example.hanmo.service.impl;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.example.hanmo.domain.MatchingGroupsEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.*;
import org.example.hanmo.dto.matching.request.RedisUserDto;
import org.example.hanmo.dto.matching.response.MatchingResponse;
import org.example.hanmo.dto.matching.response.MatchingResultResponse;
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

  // 대기 유저 Redis에 추가, 유저 정보 저장, userStatus "PENDING"
  @Transactional
  public void waitingOneToOneMatching(RedisUserDto userDto) {
    UserEntity user =
        userRepository
            .findById(userDto.getId())
            .orElseThrow(
                () -> new NotFoundException("유저를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));

    if (user.getMatchingGroup() != null) {
      throw new MatchingException("이미 매칭된 유저입니다.", ErrorCode.USER_ALREADY_MATCHED);
    }

    if (user.getMatchingType() != null && user.getMatchingType() == MatchingType.TWO_TO_TWO) {
      throw new MatchingException(
          "이미 2:2 매칭을 신청한 상태입니다. 다른 타입의 매칭을 신청할 수 없습니다.", ErrorCode.MATCHING_TYPE_CONFLICT);
    }

    if (user.getUserStatus() == UserStatus.PENDING) {
      throw new MatchingException("이미 매칭이 진행 중입니다.", ErrorCode.MATCHING_ALREADY_IN_PROGRESS);
    }

    user.setUserStatus(UserStatus.PENDING);
    user.setMatchingType(MatchingType.ONE_TO_ONE);
    userRepository.save(user);

    userDto.setUserStatus(UserStatus.PENDING);
    redisWaitingRepository.addUserToWaitingGroupInRedis(userDto, MatchingType.ONE_TO_ONE);

    log.info("1:1 매칭 신청을 시작하였습니다. Redis에 대기 데이터가 등록되었습니다.");
  }

  @Transactional
  public void waitingTwoToTwoMatching(RedisUserDto userDto) {
    UserEntity user =
        userRepository
            .findById(userDto.getId())
            .orElseThrow(
                () -> new NotFoundException("유저를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));

    if (user.getMatchingGroup() != null) {
      throw new MatchingException("이미 매칭된 유저입니다.", ErrorCode.USER_ALREADY_MATCHED);
    }

    if (user.getMatchingType() != null && user.getMatchingType() == MatchingType.ONE_TO_ONE) {
      throw new MatchingException(
          "이미 1:1 매칭을 신청한 상태입니다. 다른 타입의 매칭을 신청할 수 없습니다.", ErrorCode.MATCHING_TYPE_CONFLICT);
    }

    if (user.getUserStatus() == UserStatus.PENDING) {
      throw new MatchingException("이미 매칭이 진행 중입니다.", ErrorCode.MATCHING_ALREADY_IN_PROGRESS);
    }

    user.setUserStatus(UserStatus.PENDING);
    user.setMatchingType(MatchingType.TWO_TO_TWO);
    userRepository.save(user);

    userDto.setUserStatus(UserStatus.PENDING);
    redisWaitingRepository.addUserToWaitingGroupInRedis(userDto, MatchingType.TWO_TO_TWO);

    log.info("2:2 매칭 신청을 시작하였습니다. Redis에 대기 데이터가 등록되었습니다.");
  }

  // 1:1 매칭
  @Transactional
  public MatchingResponse matchSameGenderOneToOne(String tempToken) {
    UserEntity user = authValidate.validateTempToken(tempToken);
    Gender myGender = user.getGender();

    List<RedisUserDto> waitingUserDto =
        redisWaitingRepository.getWaitingUsers(MatchingType.ONE_TO_ONE);

    // 자기 자신 제외, 다른 학과, 같은 성별, 상태 PENDING
    List<RedisUserDto> filteredUsers =
        waitingUserDto.stream()
            .filter(u -> u.getUserStatus() == UserStatus.PENDING)
            .filter(u -> !u.getId().equals(user.getId()))
            .filter(u -> u.getGender() == myGender)
            .filter(u -> !u.getDepartment().equals(user.getDepartment()))
            .toList();

    if (filteredUsers.isEmpty()) {
      return new MatchingResponse(user.getMatchingType());
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
                        "매칭 대기열에 존재하는 유저 정보를 DB에서 찾을 수 없습니다.",
                        ErrorCode.MATCHING_NOT_FOUND_EXCEPTION));

    if (matchedUser.getUserStatus() != UserStatus.PENDING) {
      throw new MatchingException("이미 매칭된 유저입니다.", ErrorCode.USER_ALREADY_MATCHED);
    }

    // Redis 대기열에서 제거
    redisWaitingRepository.removeUserFromWaitingGroup(
        MatchingType.ONE_TO_ONE, List.of(user.toRedisUserDto(), matchedUserDto));

    // 매칭 후 대기열이 비어 있으면 전체 키 삭제
    if (redisWaitingRepository.getWaitingUsers(MatchingType.ONE_TO_ONE).isEmpty()) {
      redisWaitingRepository.clearWaitingGroup(MatchingType.ONE_TO_ONE);
    }

    return createOneToOneMatchingGroup(List.of(user, matchedUser));
  }

  // 2:2 매칭
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
            .filter(u -> !u.getId().equals(user.getId())) // 자기 자신 제외
            .filter(
                u -> {
                  if (u.getGender() == myGender) {
                    return true; // 동성은 학과 무관
                  } else {
                    return !u.getDepartment().equals(myDept); // 이성은 다른 학과
                  }
                })
            .toList();

    if (filteredUsers.size() < 3) {
      return new MatchingResponse(user.getMatchingType());
    }

    // 랜덤으로 3명 선택 (중복을 피하기 위해 Set 사용)
    Set<Integer> selectedIndexes = new HashSet<>();
    List<RedisUserDto> matchedDtos = new ArrayList<>();
    matchedDtos.add(user.toRedisUserDto()); // 자기 자신 추가

    // 3명의 유저를 선택하여 matchedDtos에 추가
    while (matchedDtos.size() < 4) {
      int randomIndex = ThreadLocalRandom.current().nextInt(filteredUsers.size());

      // 중복 체크 및 추가
      if (!selectedIndexes.contains(randomIndex)) {
        matchedDtos.add(filteredUsers.get(randomIndex));
        selectedIndexes.add(randomIndex);
      }

      // 모든 유저가 선택된 경우 루프 종료
      if (selectedIndexes.size() == filteredUsers.size()) {
        break;
      }
    }

    // Redis 대기열에서 제거
    redisWaitingRepository.removeUserFromWaitingGroup(MatchingType.TWO_TO_TWO, matchedDtos);

    if (redisWaitingRepository.getWaitingUsers(MatchingType.TWO_TO_TWO).isEmpty()) {
      redisWaitingRepository.clearWaitingGroup(MatchingType.TWO_TO_TWO);
    }

    // 매칭 대상 조회 및 상태 업데이트
    List<UserEntity> matchedUsers = new ArrayList<>();

    for (RedisUserDto dto : matchedDtos) {
      UserEntity matchedUser =
          userRepository
              .findById(dto.getId())
              .orElseThrow(
                  () -> new NotFoundException("유저를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));
      if (matchedUser.getUserStatus() != UserStatus.PENDING) {
        throw new MatchingException("이미 매칭된 유저입니다.", ErrorCode.USER_ALREADY_MATCHED);
      }

      matchedUser.setUserStatus(UserStatus.MATCHED);
      matchedUsers.add(matchedUser);
    }

    user.setUserStatus(UserStatus.MATCHED);
    matchedUsers.add(user); // 마지막에 자기 자신을 매칭된 유저 리스트에 추가

    return createTwoToTwoMatchingGroup(matchedUsers);
  }

  // 1:1 매칭 그룹 생성
  @Transactional
  public MatchingResponse createOneToOneMatchingGroup(List<UserEntity> users) {
    MatchingGroupsEntity matchingGroup =
        MatchingGroupsEntity.builder()
            .maleCount((int) users.stream().filter(u -> u.getGender() == Gender.M).count())
            .femaleCount((int) users.stream().filter(u -> u.getGender() == Gender.F).count())
            .isSameDepartment(false) // 다른 학과!
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
    List<UserEntity> femaleUsers = users.stream().filter(u -> u.getGender() == Gender.F).toList();

    // 학과 중복 체크
    if (checkDepartmentConflict(maleUsers, femaleUsers)) {
      throw new MatchingException(
          "이성 유저 간 학과가 겹칠 수 없습니다.", ErrorCode.DEPARTMENT_CONFLICT_EXCEPTION);
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
            .map(user -> new MatchingUserInfo(user.getNickname(), user.getInstagramId()))
            .toList();

    return new MatchingResponse(matchedUsers, MatchingType.ONE_TO_ONE);
  }

  // 2:2 매칭 응답 생성
  @NotNull
  private MatchingResponse createTwoToTwoMatchingResponse(List<UserEntity> users) {
    List<MatchingUserInfo> matchedUsers =
        users.stream()
            .map(user -> new MatchingUserInfo(user.getNickname(), user.getInstagramId()))
            .toList();

    return new MatchingResponse(matchedUsers, MatchingType.TWO_TO_TWO);
  }

  // 매칭 결과 조회
  public MatchingResultResponse getMatchingResult(String tempToken) {
    UserEntity user = authValidate.validateTempToken(tempToken);
    MatchingGroupsEntity matchingGroup = user.getMatchingGroup();

    if (user.getUserStatus() == UserStatus.PENDING && matchingGroup == null) {
      throw new MatchingException(
          "매칭 대기 중인 사용자입니다. 매칭 인원 수가 부족하여 매칭이 완료되지 않았습니다.",
          ErrorCode.INSUFFICIENT_USERS_FOR_MATCHING_EXCEPTION);
    }

    if (user.getUserStatus() == null && matchingGroup == null) {
      throw new MatchingException("사용자가 매칭을 시도하지 않았습니다.", ErrorCode.MATCHING_NOT_FOUND_EXCEPTION);
    }

    List<UserProfileResponseDto> users =
        matchingGroup.getUsers().stream()
            .map(
                matchedUser ->
                    new UserProfileResponseDto(
                        matchedUser.getNickname(),
                        matchedUser.getName(),
                        matchedUser.getInstagramId()))
            .collect(Collectors.toList());

    return new MatchingResultResponse(matchingGroup.getMatchingType(), users);
  }

  // 매칭 취소
  @Transactional
  public void cancelMatching(String tempToken) {
    UserEntity user = authValidate.validateTempToken(tempToken);

    if (user.getUserStatus() == UserStatus.MATCHED) {
      throw new MatchingException(
          "이미 매칭이 완료된 상태이므로 매칭을 취소할 수 없습니다.", ErrorCode.USER_ALREADY_MATCHED);
    }

    if (user.getUserStatus() != UserStatus.PENDING) {
      throw new MatchingException("매칭 대기 상태가 아니므로 취소할 수 없습니다.", ErrorCode.MATCHING_NOT_IN_PROGRESS);
    }

    MatchingType matchingType = user.getMatchingType();

    redisWaitingRepository.removeUserFromWaitingGroup(
        matchingType, Collections.singletonList(user.toRedisUserDto()));

    user.setUserStatus(null);
    user.setMatchingType(null);
    userRepository.save(user);
  }

  // 학과 중복 여부 체크
  private boolean checkDepartmentConflict(
      List<UserEntity> maleUsers, List<UserEntity> femaleUsers) {

    return maleUsers.stream()
        .anyMatch(
            male ->
                femaleUsers.stream()
                    .anyMatch(female -> male.getDepartment().equals(female.getDepartment())));
  }
}
