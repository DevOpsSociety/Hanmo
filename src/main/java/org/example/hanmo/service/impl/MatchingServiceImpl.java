package org.example.hanmo.service.impl;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.example.hanmo.domain.MatchingGroupsEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.*;
import org.example.hanmo.dto.admin.request.ManualMatchRequestDto;
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
import org.example.hanmo.redis.listener.KeyExpirationListener;
import org.example.hanmo.repository.MatchingGroupRepository;
import org.example.hanmo.repository.user.UserRepository;
import org.example.hanmo.service.MatchingService;
import org.example.hanmo.service.PreferFilterService;
import org.example.hanmo.util.ChatRoomUtil;
import org.example.hanmo.util.SmsCertificationUtil;
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
  private final SmsCertificationUtil smsCertificationUtil;
  private final PreferFilterService preferFilterService;
  private final ChatRoomUtil chatRoomUtil;

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
  public MatchingResponse matchSameGenderOneToOne(String tempToken, RedisUserDto redisUserDto) {
    UserEntity user = authValidate.validateTempToken(tempToken);
    Gender myGender = user.getGender();
    String myMbti = user.getMbti().name();
    PreferMbtiRequest myPrefer = redisUserDto.getPreferMbtiRequest();
    Integer myStudentYear = redisUserDto.getStudentYear();
    Integer myPreferredStudentYear = redisUserDto.getPreferredStudentYear();

    List<RedisUserDto> waitingUserDto = redisWaitingRepository.getWaitingUsers(MatchingType.ONE_TO_ONE, GenderMatchingType.SAME_GENDER);

    // 기본 필터 (자기 자신 제외, 다른 학과, 같은 성별, 상태 PENDING)
    List<RedisUserDto> filteredUsers =
        waitingUserDto.stream()
            .filter(u -> u.getUserStatus() == UserStatus.PENDING)
            .filter(u -> !u.getId().equals(user.getId()))
            .filter(u -> u.getGender() == myGender)
            .filter(u -> u.getDepartment() != user.getDepartment())
            .toList();

    // MBTI 선호 필터링
    List<RedisUserDto> mbtiFilteredSames = preferFilterService.filterByMbti(myMbti, myPrefer, filteredUsers);

    // 학번 연도 선호 필터링
    List<RedisUserDto> yearFilteredSames = preferFilterService.filterByMutualStudentYear(myPreferredStudentYear, myStudentYear, mbtiFilteredSames);

    // 상호 MBTI 선호가 맞는 동성 유저 필터링
    List<RedisUserDto> validOpposites = yearFilteredSames.stream()
        .filter(candidate -> {
          List<RedisUserDto> filtered = preferFilterService.filterByMbti(
                  candidate.getMbti().getMbtiType(), candidate.getPreferMbtiRequest(), yearFilteredSames);
          return !filtered.isEmpty();
        })
        .toList();


    if (validOpposites.isEmpty()) {
      return new MatchingResponse(user.getMatchingType(), user.getGenderMatchingType());
    }

    // 랜덤으로 한 명 선택
    RedisUserDto matchedUserDto =
        validOpposites.get(ThreadLocalRandom.current().nextInt(validOpposites.size()));

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
  public MatchingResponse matchDifferentGenderOneToOne(String tempToken, RedisUserDto redisUserDto) {
    UserEntity user = authValidate.validateTempToken(tempToken);
    Gender myGender = user.getGender();
    String myMbti = user.getMbti().name();
    PreferMbtiRequest myPrefer = redisUserDto.getPreferMbtiRequest();
    Integer myStudentYear = redisUserDto.getStudentYear();
    Integer myPreferredStudentYear = redisUserDto.getPreferredStudentYear();

    List<RedisUserDto> waitingUserDto = redisWaitingRepository.getWaitingUsers(MatchingType.ONE_TO_ONE, GenderMatchingType.DIFFERENT_GENDER);

    // 기본 필터 (자기 자신 제외, 다른 학과, 다른 성별, 상태 PENDING)
    List<RedisUserDto> filteredUsers =
            waitingUserDto.stream()
                    .filter(u -> u.getUserStatus() == UserStatus.PENDING)
                    .filter(u -> !u.getId().equals(user.getId()))
                    .filter(u -> u.getGender() != myGender)
                    .filter(u -> u.getDepartment() != user.getDepartment())
                    .toList();

    // MBTI 선호 필터링
    List<RedisUserDto> mbtiFilteredOpposites = preferFilterService.filterByMbti(myMbti, myPrefer, filteredUsers);

    // 학번 연도 선호 필터링
    List<RedisUserDto> yearFilteredOpposites = preferFilterService.filterByMutualStudentYear(myPreferredStudentYear, myStudentYear, mbtiFilteredOpposites);

    List<RedisUserDto> validOpposites = yearFilteredOpposites.stream()
        .filter(candidate -> {
          List<RedisUserDto> filtered = preferFilterService.filterByMbti(
                  candidate.getMbti().getMbtiType(), candidate.getPreferMbtiRequest(), yearFilteredOpposites);
          return !filtered.isEmpty();
        })
        .toList();


    if (validOpposites.isEmpty()) {
      return new MatchingResponse(user.getMatchingType(), user.getGenderMatchingType());
    }

    // 랜덤으로 한 명 선택
    RedisUserDto matchedUserDto =
        validOpposites.get(ThreadLocalRandom.current().nextInt(validOpposites.size()));

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
    Integer myStudentYear = redisUserDto.getStudentYear();
    Integer myPreferredStudentYear = redisUserDto.getPreferredStudentYear();

    // 대기 중인 유저 가져오기
    List<RedisUserDto> waitingUsers = redisWaitingRepository.getWaitingUsers(MatchingType.TWO_TO_TWO, GenderMatchingType.DIFFERENT_GENDER);

    // 기본 필터
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

    // 동성과 이성 분리
    // 동성
    List<RedisUserDto> sameGenderList = filteredUsers.stream()
        .filter(u -> u.getGender() == myGender)
        .toList();

    // 이성
    List<RedisUserDto> oppositeGenderList = filteredUsers.stream()
        .filter(u -> u.getGender() != myGender)
        .toList();

    // 이성 후보 MBTI 선호 필터링
    List<RedisUserDto> filteredOpposites = preferFilterService.filterByMbti(myMbti, myPrefer, oppositeGenderList);

    // 이성 후보 학번 연도 선호 필터링
    List<RedisUserDto> yearFilteredOpposites =
            preferFilterService.filterByMutualStudentYear(
                    myPreferredStudentYear,
                    myStudentYear,
                    filteredOpposites
            );

    // 동성 후보끼리는 학번 선호 필터링 X
    // 동성 리스트 중 이성과 MBTI 조건이 상호 맞는 경우만 필터링
    List<RedisUserDto> validSameGenderList = sameGenderList.stream()
        .filter(same -> {
          List<RedisUserDto> mbtiMatched = preferFilterService.filterByMbti(
              same.getMbti().getMbtiType(),
              same.getPreferMbtiRequest(),
              yearFilteredOpposites);

          // 동성 유저와 이성 유저 후보 간 학번 상호 검사
          List<RedisUserDto> yearMbtiMatched = mbtiMatched.stream()
                  .filter(candidate -> {
                    Integer candYear = candidate.getStudentYear();
                    Integer candPref = candidate.getPreferredStudentYear();
                    Integer sameYear = same.getStudentYear();
                    Integer samePref = same.getPreferredStudentYear();

                    boolean ok1 = (samePref == null) || (candYear != null && Math.abs(candYear - samePref) <= 1);
                    boolean ok2 = (candPref == null) || (sameYear != null && Math.abs(sameYear - candPref) <= 1);
                    return ok1 && ok2;
                  })
                  .toList();

          return yearMbtiMatched.size() >= 2;
        })
        .toList();

    if (validSameGenderList.isEmpty() || yearFilteredOpposites.size() < 2) {
      return new MatchingResponse(user.getMatchingType(), user.getGenderMatchingType());
    }

    // 랜덤 동성 1명 + 이성 2명 선택
    // 랜덤 동성 1명
    RedisUserDto matchedSame = validSameGenderList.get(ThreadLocalRandom.current().nextInt(validSameGenderList.size()));

    // 이성 2명 matchedSame 기준으로도 필터링 (추가 체크)
    List<RedisUserDto> finalOpposites = preferFilterService.filterByMbti(
        matchedSame.getMbti().name(),
        matchedSame.getPreferMbtiRequest(),
        yearFilteredOpposites
    );

    // 동성 유저와 이성 후보들 간 학번 선호 재검사
    List<RedisUserDto> finalYearFiltered = new ArrayList<>(finalOpposites.stream()
            .filter(candidate -> {
                Integer candYear = candidate.getStudentYear();
                Integer candPref = candidate.getPreferredStudentYear();
                Integer sameYear = matchedSame.getStudentYear();
                Integer samePref = matchedSame.getPreferredStudentYear();

                boolean ok1 = (samePref == null) || (candYear != null && Math.abs(candYear - samePref) <= 1);
                boolean ok2 = (candPref == null) || (sameYear != null && Math.abs(sameYear - candPref) <= 1);
                return ok1 && ok2;
            })
            .toList());

    // 동성(matchedSame)기준 필터링 후 검증
    if (finalYearFiltered.size() < 2) {
      return new MatchingResponse(user.getMatchingType(), user.getGenderMatchingType());
    }

    Collections.shuffle(finalYearFiltered);  // 리스트를 무작위로 섞는 함수
    List<RedisUserDto> selectedOpposites = finalYearFiltered.subList(0, 2);  // 앞에서 두명 뽑기

    // 뽑은 3명 matchedDtos 배열에 넣기
    List<RedisUserDto> matchedDtos = new ArrayList<>();
    matchedDtos.add(matchedSame); // 동성 1명
    matchedDtos.add(user.toRedisUserDto()); // 자기자신
    matchedDtos.addAll(selectedOpposites);  // 이성 2명

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

    // matchedDtos 여기에 자기자신 추가해서 코드 뺌(레디스 대기열에 자기자신도 빼야되서 matchedDtos에 넣음)

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

    List<Long> userIds = users.stream()
        .map(UserEntity::getId)
        .toList();

    chatRoomUtil.createChatRoom(matchingGroup.getMatchingGroupId(), userIds, COOLDOWN_DURATION);

    for (UserEntity u : users) {
      u.setUserStatus(UserStatus.MATCHED);
      u.setMatchingType(MatchingType.ONE_TO_ONE);
      u.setGenderMatchingType(GenderMatchingType.SAME_GENDER);
      userRepository.save(u);

      smsCertificationUtil.sendMatchingSuccessSms(u.getPhoneNumber(), u.getNickname());

      // 24시간 쿨다운 키 설정
      String key = "match:cooldown:1to1:" + u.getId();
      stringRedisTemplate.opsForValue().set(key, "1", COOLDOWN_DURATION);
    }

    List<MatchingUserInfo> infos = users.stream()
        .map(u -> new MatchingUserInfo(u.getNickname(), u.getInstagramId()))
        .toList();

    return new MatchingResponse(
        matchingGroup.getMatchingGroupId(),
        infos,
        MatchingType.ONE_TO_ONE,
        GenderMatchingType.DIFFERENT_GENDER
    );
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

    List<Long> userIds = users.stream()
        .map(UserEntity::getId)
        .toList();

    chatRoomUtil.createChatRoom(matchingGroup.getMatchingGroupId(), userIds, COOLDOWN_DURATION);

    for (UserEntity u : users) {
      u.setUserStatus(UserStatus.MATCHED);
      u.setMatchingType(MatchingType.ONE_TO_ONE);
      u.setGenderMatchingType(GenderMatchingType.DIFFERENT_GENDER);
      userRepository.save(u);

      smsCertificationUtil.sendMatchingSuccessSms(u.getPhoneNumber(), u.getNickname());
      // 24시간 쿨다운 키 설정
      String key = "match:cooldown:1to1:" + u.getId();
      stringRedisTemplate.opsForValue().set(key, "1", COOLDOWN_DURATION);
    }

    List<MatchingUserInfo> infos = users.stream()
        .map(u -> new MatchingUserInfo(u.getNickname(), u.getInstagramId()))
        .toList();

    return new MatchingResponse(
        matchingGroup.getMatchingGroupId(),
        infos,
        MatchingType.ONE_TO_ONE,
        GenderMatchingType.DIFFERENT_GENDER
    );
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

    List<Long> userIds = users.stream()
        .map(UserEntity::getId)
        .toList();

    chatRoomUtil.createChatRoom(matchingGroup.getMatchingGroupId(), userIds, COOLDOWN_DURATION);

    // 예: 2:2 매칭 그룹 생성 부분
    for (UserEntity u : users) {
      u.setUserStatus(UserStatus.MATCHED);
      u.setMatchingType(MatchingType.TWO_TO_TWO);
      u.setGenderMatchingType(GenderMatchingType.DIFFERENT_GENDER);
      userRepository.save(u);

      smsCertificationUtil.sendMatchingSuccessSms(u.getPhoneNumber(), u.getNickname());

      String key = "match:cooldown:2to2:" + u.getId();
      stringRedisTemplate.opsForValue().set(key, "1", COOLDOWN_DURATION);
    }

    List<MatchingUserInfo> infos = users.stream()
        .map(u -> new MatchingUserInfo(u.getNickname(), u.getInstagramId()))
        .toList();

    return new MatchingResponse(
        matchingGroup.getMatchingGroupId(),
        infos,
        MatchingType.TWO_TO_TWO,
        GenderMatchingType.DIFFERENT_GENDER
    );
  }

  // 1:1 동성 매칭 응답 생성
  @NotNull
  private MatchingResponse createSameGenderOneToOneMatchingResponse(Long roomId,List<UserEntity> users) {
    return createMatchingResponse(roomId,users, MatchingType.ONE_TO_ONE, GenderMatchingType.SAME_GENDER);
  }

  // 1:1 이성 매칭 응답 생성
  @NotNull
  private MatchingResponse createDifferentGenderOneToOneMatchingResponse(Long roomId,List<UserEntity> users) {
    return createMatchingResponse(roomId,users, MatchingType.ONE_TO_ONE, GenderMatchingType.DIFFERENT_GENDER);
  }

  // 2:2 매칭 응답 생성
  @NotNull
  private MatchingResponse createTwoToTwoMatchingResponse(Long roomId,List<UserEntity> users) {
    return createMatchingResponse(roomId,users, MatchingType.TWO_TO_TWO, GenderMatchingType.DIFFERENT_GENDER);
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
                        matchedUser.getInstagramId(),
                        matchedUser.getUserRole()
                    ))
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

  // 매칭 응답 생성
  @NotNull
  private MatchingResponse createMatchingResponse(Long roomId,List<UserEntity> users, MatchingType matchingType, GenderMatchingType genderMatchingType) {
    List<MatchingUserInfo> matchedUsers = users.stream()
        .map(user -> new MatchingUserInfo(user.getNickname(), user.getInstagramId()))
        .toList();

    return new MatchingResponse(
        roomId,
        matchedUsers,
        matchingType,
        genderMatchingType
    );  }

  //어드민이 유저 삭제시 나머지 유저들의 매칭값 null
  @Override
  public void cleanupAfterUserDeletion(String nickname) {
    UserEntity user = userRepository.findByNickname(nickname)
            .orElseThrow(() -> new NotFoundException("삭제할 사용자를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));
    MatchingGroupsEntity group = user.getMatchingGroup();
    if (group == null) {
      return;
    }
    List<UserEntity> others = group.getUsers().stream()
            .filter(member -> !member.getId().equals(user.getId()))
            .collect(Collectors.toList());
    others.forEach(member -> {
      member.setMatchingGroup(null);
      member.setUserStatus(null);
      member.setMatchingType(null);
      member.setGenderMatchingType(null);
    });
    userRepository.saveAll(others);
    matchingGroupRepository.delete(group);
  }

  @Override
  @Transactional
  public MatchingResponse manualMatch(ManualMatchRequestDto requestDto) {
    MatchingType matchingType       = requestDto.getMatchingType();
    GenderMatchingType genderType   = requestDto.getGenderMatchingType();
    List<String> userIdStrings      = requestDto.getUserIds();

    // 2) Redis 대기열에서 사용자 제거
    List<RedisUserDto> redisUsers = userIdStrings.stream()
            .map(idStr -> RedisUserDto.builder()
                    .id(Long.parseLong(idStr))        // String → Long 파싱
                    .userStatus(UserStatus.PENDING)
                    .build())
            .collect(Collectors.toList());
    redisWaitingRepository.removeUserFromWaitingGroup(matchingType, genderType, redisUsers);

    // 3) DB 조회 및 상태 검증
    List<UserEntity> matchedUsers = userIdStrings.stream()
            .map(idStr -> {
              Long userId = Long.parseLong(idStr);
              return userRepository.findById(userId)
                      .orElseThrow(() -> new NotFoundException("유저 없음: " + userId, ErrorCode.NOT_FOUND_EXCEPTION));
            })
            .peek(user -> {
              if (user.getUserStatus() == UserStatus.MATCHED) {
                throw new MatchingException("이미 매칭된 유저: " + user.getId(), ErrorCode.USER_ALREADY_MATCHED);
              }
              user.setUserStatus(UserStatus.MATCHED);
              user.setMatchingType(matchingType);
              user.setGenderMatchingType(genderType);
              userRepository.save(user);
            })
            .collect(Collectors.toList());

    // 4) 매칭 그룹 생성 및 저장
    MatchingGroupsEntity group = MatchingGroupsEntity.builder()
            .maleCount((int) matchedUsers.stream()
                    .filter(u -> u.getGender() == Gender.M).count())
            .femaleCount((int) matchedUsers.stream()
                    .filter(u -> u.getGender() == Gender.F).count())
            .matchingType(matchingType)
            .genderMatchingType(genderType)
            .groupStatus(GroupStatus.MATCHED)
            .isSameDepartment(false)
            .build();
    matchedUsers.forEach(group::addUser);
    matchingGroupRepository.save(group);

    // 5) 쿨다운 키 설정
    String cooldownKeyPrefix = matchingType == MatchingType.ONE_TO_ONE
            ? KeyExpirationListener.COOLDOWN_1TO1_PREFIX
            : KeyExpirationListener.COOLDOWN_2TO2_PREFIX;
    matchedUsers.forEach(user -> {
      stringRedisTemplate.opsForValue().set(cooldownKeyPrefix + user.getId(), "1", COOLDOWN_DURATION);

      smsCertificationUtil.sendMatchingSuccessSms(user.getPhoneNumber(), user.getNickname());

    });

    List<MatchingUserInfo> userInfos = matchedUsers.stream()
            .map(u -> new MatchingUserInfo(u.getNickname(), u.getInstagramId()))
            .collect(Collectors.toList());
    List<Long> userIds = matchedUsers.stream().map(UserEntity::getId).toList();
    chatRoomUtil.createChatRoom(group.getMatchingGroupId(), userIds, COOLDOWN_DURATION);

    return new MatchingResponse(
        group.getMatchingGroupId(),
        userInfos,
        group.getMatchingType(),
        group.getGenderMatchingType()
    );
  }
}
