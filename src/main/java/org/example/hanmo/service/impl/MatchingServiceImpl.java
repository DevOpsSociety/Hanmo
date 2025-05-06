package org.example.hanmo.service.impl;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.example.hanmo.domain.MatchingGroupsEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.*;
import org.example.hanmo.dto.matching.request.PreferMbtiRequest;
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
import org.example.hanmo.service.PreferFilterService;
import org.example.hanmo.vaildate.AuthValidate;
import org.example.hanmo.vaildate.UserValidate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
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
  private final StringRedisTemplate stringRedisTemplate;
  private final UserValidate userValidate;
  private final PreferFilterService preferFilterService;

  // 쿨다운 키를 하루로 지정
  private static final Duration COOLDOWN_DURATION = Duration.ofDays(1);

  // 1:1 동성 매칭 대기열 등록
  @Transactional
  public void waitingSameGenderOneToOneMatching(RedisUserDto userDto) {
    validateAndApplyMatchingRequest(userDto, MatchingType.ONE_TO_ONE, GenderMatchingType.SAME_GENDER);
  }

  // 1:1 이성 매칭 대기열 등록
  @Transactional
  public void waitingDifferentGenderOneToOneMatching(RedisUserDto userDto) {
    validateAndApplyMatchingRequest(userDto, MatchingType.ONE_TO_ONE, GenderMatchingType.DIFFERENT_GENDER);
  }

  // 2:2 이성 매칭 대기열 등록
  @Transactional
  public void waitingTwoToTwoMatching(RedisUserDto userDto) {
    validateAndApplyMatchingRequest(userDto, MatchingType.TWO_TO_TWO, GenderMatchingType.DIFFERENT_GENDER);
  }

  // 1:1 동성 매칭 수행, 매칭 그룹 생성하여 반환
  @Transactional
  public MatchingResponse matchSameGenderOneToOne(String tempToken) {
    UserEntity user = authValidate.validateTempToken(tempToken);
    Gender myGender = user.getGender();

    List<RedisUserDto> waitingUserDto = redisWaitingRepository.getWaitingUsers(MatchingType.ONE_TO_ONE, GenderMatchingType.SAME_GENDER);

    // 자기 자신 제외, 다른 학과, 같은 성별, 상태 PENDING
    List<RedisUserDto> filteredUsers =
        waitingUserDto.stream()
            .filter(u -> u.getUserStatus() == UserStatus.PENDING)
            .filter(u -> !u.getId().equals(user.getId()))
            .filter(u -> u.getGender() == myGender)
            .filter(u -> u.getDepartment() != user.getDepartment())
            .toList();

    if (filteredUsers.isEmpty()) {
      return new MatchingResponse(user.getMatchingType(), user.getGenderMatchingType());
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
                        "매칭 대상 유저(ID: " + matchedUserDto.getId() + ")를 DB에서 찾을 수 없습니다.",
                        ErrorCode.MATCHING_NOT_FOUND_EXCEPTION));

    if (matchedUser.getUserStatus() != UserStatus.PENDING) {
      throw new MatchingException("이미 매칭된 유저입니다.", ErrorCode.USER_ALREADY_MATCHED);
    }

    // Redis 대기열에서 제거
    redisWaitingRepository.removeUserFromWaitingGroup(
        MatchingType.ONE_TO_ONE, GenderMatchingType.SAME_GENDER, List.of(user.toRedisUserDto(), matchedUserDto));

    // 매칭 후 대기열이 비어 있으면 전체 키 삭제
    if (redisWaitingRepository.getWaitingUsers(MatchingType.ONE_TO_ONE, GenderMatchingType.SAME_GENDER).isEmpty()) {
      redisWaitingRepository.clearWaitingGroup(MatchingType.ONE_TO_ONE, GenderMatchingType.SAME_GENDER);
    }

    return createSameGenderOneToOneMatchingGroup(List.of(user, matchedUser));
  }

  // 1:1 이성 매칭 수행, 매칭 그룹 생성하여 반환
  @Transactional
  public MatchingResponse matchDifferentGenderOneToOne(String tempToken) {
    UserEntity user = authValidate.validateTempToken(tempToken);
    Gender myGender = user.getGender();

    List<RedisUserDto> waitingUserDto = redisWaitingRepository.getWaitingUsers(MatchingType.ONE_TO_ONE, GenderMatchingType.DIFFERENT_GENDER);

    // 자기 자신 제외, 다른 학과, 다른 성별, 상태 PENDING
    List<RedisUserDto> filteredUsers =
            waitingUserDto.stream()
                    .filter(u -> u.getUserStatus() == UserStatus.PENDING)
                    .filter(u -> !u.getId().equals(user.getId()))
                    .filter(u -> u.getGender() != myGender)
                    .filter(u -> u.getDepartment() != user.getDepartment())
                    .toList();

    if (filteredUsers.isEmpty()) {
      return new MatchingResponse(user.getMatchingType(), user.getGenderMatchingType());
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
                                            "매칭 대상 유저(ID: " + matchedUserDto.getId() + ")를 DB에서 찾을 수 없습니다.",
                                            ErrorCode.MATCHING_NOT_FOUND_EXCEPTION));

    if (matchedUser.getUserStatus() != UserStatus.PENDING) {
      throw new MatchingException("이미 매칭된 유저입니다.", ErrorCode.USER_ALREADY_MATCHED);
    }


    // Redis 대기열에서 제거
    redisWaitingRepository.removeUserFromWaitingGroup(MatchingType.ONE_TO_ONE, GenderMatchingType.DIFFERENT_GENDER, List.of(user.toRedisUserDto(), matchedUserDto));

    // 매칭 후 대기열이 비어 있으면 전체 키 삭제
    if (redisWaitingRepository.getWaitingUsers(MatchingType.ONE_TO_ONE, GenderMatchingType.DIFFERENT_GENDER).isEmpty()) {
      redisWaitingRepository.clearWaitingGroup(MatchingType.ONE_TO_ONE, GenderMatchingType.DIFFERENT_GENDER);
    }

    return createDifferentGenderOneToOneMatchingGroup(List.of(user, matchedUser));
  }

  // 2:2 매칭 수행, 매칭 그룹 생성하여 반환
  @Transactional
  public MatchingResponse matchDifferentGenderTwoToTwo(String tempToken, RedisUserDto redisUserDto) {
    UserEntity user = authValidate.validateTempToken(tempToken);
    Gender myGender = user.getGender();
    Department myDept = user.getDepartment();
    String myMbti = user.getMbti().name();
    PreferMbtiRequest myPrefer = redisUserDto.getPreferMbtiRequest();

    // 대기 중인 유저 가져오기
    List<RedisUserDto> waitingUsers = redisWaitingRepository.getWaitingUsers(MatchingType.TWO_TO_TWO, GenderMatchingType.DIFFERENT_GENDER);
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

    List<RedisUserDto> mbtiFilteredUsers = preferFilterService.filterByMbti(myMbti, myPrefer, filteredUsers);

    if (mbtiFilteredUsers.size() < 3) {
      return new MatchingResponse(user.getMatchingType(), user.getGenderMatchingType());
    }

    // 랜덤으로 3명 선택
    Set<Integer> selectedIndexes = new HashSet<>();
    List<RedisUserDto> matchedDtos = new ArrayList<>();
    matchedDtos.add(redisUserDto); // 자기 자신 추가

    // 3명의 유저를 선택하여 matchedDtos에 추가
    while (matchedDtos.size() < 4) {
      int randomIndex = ThreadLocalRandom.current().nextInt(mbtiFilteredUsers.size());

      // 중복 체크 및 추가
      if (!selectedIndexes.contains(randomIndex)) {
        matchedDtos.add(mbtiFilteredUsers.get(randomIndex));
        selectedIndexes.add(randomIndex);
      }

      // 모든 유저가 선택된 경우 루프 종료
      if (selectedIndexes.size() == mbtiFilteredUsers.size()) {
        break;
      }
    }

    // DB 상태 변경 및 매칭된 유저들 처리
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

      // 매칭된 유저 상태 변경
      matchedUser.setUserStatus(UserStatus.MATCHED);
      matchedUsers.add(matchedUser);
    }

    // 자기 자신 상태 변경, 마지막에 자기 자신을 매칭된 유저 리스트에 추가
    user.setUserStatus(UserStatus.MATCHED);
    matchedUsers.add(user);

    // Redis 대기열에서 제거
    redisWaitingRepository.removeUserFromWaitingGroup(MatchingType.TWO_TO_TWO, GenderMatchingType.DIFFERENT_GENDER, matchedDtos);

    if (redisWaitingRepository.getWaitingUsers(MatchingType.TWO_TO_TWO, GenderMatchingType.DIFFERENT_GENDER).isEmpty()) {
      redisWaitingRepository.clearWaitingGroup(MatchingType.TWO_TO_TWO, GenderMatchingType.DIFFERENT_GENDER);
    }

    return createTwoToTwoMatchingGroup(matchedUsers);
  }

  // 1:1 동성 매칭 그룹 생성 및 생성된 정보 반환
  @Transactional
  public MatchingResponse createSameGenderOneToOneMatchingGroup(List<UserEntity> users) {
    MatchingGroupsEntity matchingGroup =
            MatchingGroupsEntity.builder()
                    .maleCount((int) users.stream().filter(u -> u.getGender() == Gender.M).count())
                    .femaleCount((int) users.stream().filter(u -> u.getGender() == Gender.F).count())
                    .isSameDepartment(false) // 다른 학과!
                    .groupStatus(GroupStatus.MATCHED)
                    .matchingType(MatchingType.ONE_TO_ONE)
                    .genderMatchingType(GenderMatchingType.SAME_GENDER)
                    .build();

    matchingGroup.addUser(users.get(0));
    matchingGroup.addUser(users.get(1));
    matchingGroupRepository.save(matchingGroup);

    for (UserEntity u : users) {
      u.setUserStatus(UserStatus.MATCHED);
      u.setMatchingType(MatchingType.ONE_TO_ONE);
      u.setGenderMatchingType(GenderMatchingType.SAME_GENDER);
      userRepository.save(u);

      // 24시간 쿨다운 키 설정
      String key = "match:cooldown:1to1:" + u.getId();
      stringRedisTemplate.opsForValue().set(key, "1", COOLDOWN_DURATION);
    }

    return createSameGenderOneToOneMatchingResponse(users);
  }

  // 1:1 이성 매칭 그룹 생성 및 생성된 정보 반환
  @Transactional
  public MatchingResponse createDifferentGenderOneToOneMatchingGroup(List<UserEntity> users) {
    MatchingGroupsEntity matchingGroup =
            MatchingGroupsEntity.builder()
                    .maleCount((int) users.stream().filter(u -> u.getGender() == Gender.M).count())
                    .femaleCount((int) users.stream().filter(u -> u.getGender() == Gender.F).count())
                    .isSameDepartment(false) // 다른 학과!
                    .groupStatus(GroupStatus.MATCHED)
                    .matchingType(MatchingType.ONE_TO_ONE)
                    .genderMatchingType(GenderMatchingType.DIFFERENT_GENDER)
                    .build();

    matchingGroup.addUser(users.get(0));
    matchingGroup.addUser(users.get(1));
    matchingGroupRepository.save(matchingGroup);

    for (UserEntity u : users) {
      u.setUserStatus(UserStatus.MATCHED);
      u.setMatchingType(MatchingType.ONE_TO_ONE);
      u.setGenderMatchingType(GenderMatchingType.DIFFERENT_GENDER);
      userRepository.save(u);

      // 24시간 쿨다운 키 설정
      String key = "match:cooldown:1to1:" + u.getId();
      stringRedisTemplate.opsForValue().set(key, "1", COOLDOWN_DURATION);
    }

    return createDifferentGenderOneToOneMatchingResponse(users);
  }

  // 2:2 매칭 그룹 생성 및 생성된 정보 반환
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
                    .genderMatchingType(GenderMatchingType.DIFFERENT_GENDER)
                    .build();

    matchingGroup.addUser(maleUsers.get(0));
    matchingGroup.addUser(maleUsers.get(1));
    matchingGroup.addUser(femaleUsers.get(0));
    matchingGroup.addUser(femaleUsers.get(1));
    matchingGroupRepository.save(matchingGroup);

    // 예: 2:2 매칭 그룹 생성 부분
    for (UserEntity u : users) {
      u.setUserStatus(UserStatus.MATCHED);
      u.setMatchingType(MatchingType.TWO_TO_TWO);
      u.setGenderMatchingType(GenderMatchingType.DIFFERENT_GENDER);
      userRepository.save(u);

      String key = "match:cooldown:2to2:" + u.getId();
      stringRedisTemplate.opsForValue().set(key, "1", COOLDOWN_DURATION);
    }

    return createTwoToTwoMatchingResponse(users);
  }

  // 1:1 동성 매칭 응답 생성
  @NotNull
  private MatchingResponse createSameGenderOneToOneMatchingResponse(List<UserEntity> users) {
    return createMatchingResponse(users, MatchingType.ONE_TO_ONE, GenderMatchingType.SAME_GENDER);
  }

  // 1:1 이성 매칭 응답 생성
  @NotNull
  private MatchingResponse createDifferentGenderOneToOneMatchingResponse(List<UserEntity> users) {
    return createMatchingResponse(users, MatchingType.ONE_TO_ONE, GenderMatchingType.DIFFERENT_GENDER);
  }

  // 2:2 매칭 응답 생성
  @NotNull
  private MatchingResponse createTwoToTwoMatchingResponse(List<UserEntity> users) {
    return createMatchingResponse(users, MatchingType.TWO_TO_TWO, GenderMatchingType.DIFFERENT_GENDER);
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

    return new MatchingResultResponse(user.getUserStatus(), matchingGroup.getMatchingType(), matchingGroup.getGenderMatchingType(), users);
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
    GenderMatchingType genderMatchingType = user.getGenderMatchingType();

    redisWaitingRepository.removeUserFromWaitingGroup(
        matchingType, genderMatchingType, Collections.singletonList(user.toRedisUserDto()));

    user.setUserStatus(null);
    user.setMatchingType(null);
    user.setGenderMatchingType(null);
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

  // 매칭 요청한 사용자 검증 및 DB에 저장
  private void validateAndApplyMatchingRequest(RedisUserDto userDto, MatchingType matchingType, GenderMatchingType genderMatchingType) {
    userValidate.validateMatchingCooldown(userDto.getId(), matchingType);
    UserEntity user = userRepository.findById(userDto.getId())
                    .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));

    if (user.getMatchingGroup() != null) {
      throw new MatchingException("이미 매칭된 유저입니다.", ErrorCode.USER_ALREADY_MATCHED);
    }

    // 매칭 타입 검증 (1:1 / 2:2)
    if (user.getMatchingType() != null && user.getMatchingType() != matchingType) {
      String matchingTypeMessage = (matchingType == MatchingType.ONE_TO_ONE)
              ? "2:2 매칭을 이미 신청한 상태입니다. 다른 타입의 매칭을 신청할 수 없습니다."
              : "1:1 매칭을 이미 신청한 상태입니다. 다른 타입의 매칭을 신청할 수 없습니다.";
      throw new MatchingException(matchingTypeMessage, ErrorCode.MATCHING_TYPE_CONFLICT);
    }

    // 성별 매칭 타입 검증 (동성 / 이성)
    if (user.getGenderMatchingType() != null && user.getGenderMatchingType() != genderMatchingType) {
      String genderMatchingTypeMessage = (genderMatchingType == GenderMatchingType.SAME_GENDER)
              ? "이성 매칭을 이미 신청한 상태입니다. 다른 성별 매칭을 신청할 수 없습니다."
              : "동성 매칭을 이미 신청한 상태입니다. 다른 성별 매칭을 신청할 수 없습니다.";
      throw new MatchingException(genderMatchingTypeMessage, ErrorCode.GENDER_MATCHING_TYPE_CONFLICT);
    }

    if (user.getUserStatus() == UserStatus.PENDING) {
      throw new MatchingException("이미 매칭이 진행 중입니다.", ErrorCode.MATCHING_ALREADY_IN_PROGRESS);
    }

    user.setUserStatus(UserStatus.PENDING);
    user.setMatchingType(matchingType);
    user.setGenderMatchingType(genderMatchingType);
    userRepository.save(user);

    userDto.setUserStatus(UserStatus.PENDING);
    redisWaitingRepository.addUserToWaitingGroupInRedis(userDto, matchingType, genderMatchingType);
  }

  /*
  // 매칭 그룹 생성
  private void createMatchingGroup(List<UserEntity> users, MatchingType matchingType, boolean isSameGenderMatching) {
    List<UserEntity> maleUsers = users.stream().filter(u -> u.getGender() == Gender.M).toList();
    List<UserEntity> femaleUsers = users.stream().filter(u -> u.getGender() == Gender.F).toList();

    if (matchingType == MatchingType.ONE_TO_ONE || (matchingType == MatchingType.TWO_TO_TWO && !isSameGenderMatching)) {
      if (checkDepartmentConflict(maleUsers, femaleUsers)) {
        throw new MatchingException("이성 유저 간 학과가 겹칠 수 없습니다.", ErrorCode.DEPARTMENT_CONFLICT_EXCEPTION);
      }
    }

    MatchingGroupsEntity matchingGroup = MatchingGroupsEntity.builder()
            .maleCount(maleUsers.size())
            .femaleCount(femaleUsers.size())
            .isSameDepartment(isSameGenderMatching) // 동성 매칭이면 true, 이성이면 false
            .groupStatus(GroupStatus.MATCHED)
            .matchingType(matchingType)
            .genderMatchingType(isSameGenderMatching ? GenderMatchingType.SAME_GENDER : GenderMatchingType.DIFFERENT_GENDER)
            .build();

  matchingGroupRepository.save(matchingGroup);

    for (UserEntity user : users) {
      user.setUserStatus(UserStatus.MATCHED);
      userRepository.save(user);
      String key = "match:cooldown:" + matchingType.name() + ":" + user.getId();
      stringRedisTemplate.opsForValue().set(key, "1", COOLDOWN_DURATION);
    }
  }
   */

  // 매칭 응답 생성
  @NotNull
  private MatchingResponse createMatchingResponse(List<UserEntity> users, MatchingType matchingType, GenderMatchingType genderMatchingType) {
    List<MatchingUserInfo> matchedUsers =
            users.stream()
                    .map(user -> new MatchingUserInfo(user.getNickname(), user.getInstagramId()))
                    .toList();

    return new MatchingResponse(matchedUsers, matchingType, genderMatchingType);
  }
}
