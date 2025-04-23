package org.example.hanmo.service.impl;

import java.util.List;
import java.util.UUID;

import org.example.hanmo.domain.MatchingGroupsEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.UserStatus;
import org.example.hanmo.dto.user.request.UserLoginRequestDto;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.dto.user.response.UserProfileResponseDto;
import org.example.hanmo.dto.user.response.UserSignUpResponseDto;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.redis.RedisWaitingRepository;
import org.example.hanmo.repository.MatchingGroupRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.UserService;
import org.example.hanmo.vaildate.AuthValidate;
import org.example.hanmo.vaildate.SmsValidate;
import org.example.hanmo.vaildate.UserValidate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final RedisSmsRepository redisSmsRepository;
  private final RedisTempRepository redisTempRepository;
  private final UserValidate userValidate;
  private final AuthValidate authValidate;
  private final RedisWaitingRepository redisWaitingRepository;
  private final MatchingGroupRepository matchingGroupRepository;

  @Override
  public UserSignUpResponseDto signUpUser(UserSignUpRequestDto signUpRequestDto) {
    String phoneNumber = signUpRequestDto.getPhoneNumber();
    String studentNumber = signUpRequestDto.getStudentNumber();
    // SMS 인증 완료 플래그와 중복 가입 여부를 검증 (전화번호 기준)
    SmsValidate.validateSignUp(phoneNumber, redisSmsRepository, userRepository);
    // 계정 상태 점검 (이미 가입이거나, 탈퇴 3일 이내인경우)
    userValidate.validateAccountForRegistration(phoneNumber);
    // 학번 검증, 올바른 형식인지,이미 가입이 되어있는지
    UserValidate.validateStudentNumberFormat(studentNumber);
    UserValidate.validateDuplicateStudentNumber(studentNumber, userRepository);
    UserEntity user = signUpRequestDto.SignUpToUserEntity();
    // 랜덤 닉네임
    UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository);
    redisSmsRepository.deleteVerifiedFlag(phoneNumber);
    // 임시 토큰을 생성 (UUID 사용) (TTL 5분)
    String tempToken = UUID.randomUUID().toString();
    redisTempRepository.setTempToken(phoneNumber, tempToken, 5 * 60);
    userRepository.save(user);

    return new UserSignUpResponseDto(user.getNickname(), user.getPhoneNumber());
  }

  @Override
  public UserSignUpResponseDto changeNickname(String tempToken) {
    // 임시 토큰으로부터 전화번호를 검증 및 조회합니다.
    String phoneNumber =
        UserValidate.validatePhoneNumberByTempToken(tempToken, redisTempRepository);
    UserEntity user = UserValidate.getUserByPhoneNumber(phoneNumber, userRepository);
    // 닉네임 이미 변경 된 경우 예외처리
    UserValidate.validateNicknameNotChanged(user);

    UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository);
    user.setNicknameChanged(true);

    user = userRepository.save(user);
    //        redisTempRepository.deleteTempToken(tempToken);
    return new UserSignUpResponseDto(user.getNickname(), user.getPhoneNumber());
  }

  @Override
  public void withdrawUser(String phoneNumber) {
    userValidate.validateAccountCanBeDeactivated(phoneNumber);
    UserEntity user = UserValidate.getUserByPhoneNumber(phoneNumber, userRepository);
    MatchingGroupsEntity group = user.getMatchingGroup();

    // 탈퇴시 그룹삭제, 나머지 멤버 리셋함, 이건 매칭이 완료된 상태에서 탈퇴시
    if (group != null) {
      // 1) 그룹 모든 멤버에서 FK 해제 + 상태 초기화
      for (UserEntity u : group.getUsers()) {
        if (!u.getId().equals(user.getId())) {
          u.setMatchingGroup(null);
          u.setUserStatus(null);
          u.setMatchingType(null);
        }
      }
      userRepository.saveAll(group.getUsers());

      matchingGroupRepository.delete(group);
    }

    // PENDING 대기열 처리
    if (user.getUserStatus() == UserStatus.PENDING && user.getMatchingType() != null) {
      redisWaitingRepository.removeUserFromWaitingGroup(
              user.getMatchingType(), List.of(user.toRedisUserDto()));
      user.setUserStatus(null);
      user.setMatchingType(null);
      userRepository.save(user);
    }
    // 회원의 상태를 휴면 상태로 변경후 저장함
    user.deactivateAccount();
    userRepository.save(user);
    redisSmsRepository.deleteVerifiedFlag(phoneNumber);
  } // Redis에 저장된 인증 완료 플래그 삭제 (있을 경우)

  @Override
  public String loginUser(UserLoginRequestDto requestDto) {
    UserEntity user =
        userValidate.findByPhoneNumberAndStudentNumber(
            requestDto.getPhoneNumber(), requestDto.getStudentNumber());
    // 로그인 할 때 계정 활성화 상태인지 Active상태인지 점검함
    UserValidate.validateUserIsActive(user);
    String tempToken = redisTempRepository.createTempTokenForUser(user.getPhoneNumber(), true);
    return tempToken;
  }

  @Override
  public UserProfileResponseDto getUserProfile(String tempToken) {
    UserEntity user = authValidate.validateTempToken(tempToken);
    return new UserProfileResponseDto(user.getNickname(), user.getName(), user.getInstagramId());
  }

  @Override
  public void logout(String tempToken) {
    authValidate.validateTempToken(tempToken); // 토큰을 검증하고, 삭제
    redisTempRepository.deleteTempToken(tempToken);
  }

  // 복구가 가능한(3일 이내) 상태이면 다시 복구해줌
  @Override
  public void restoreUserAccount(String phoneNumber) {
    // 복구가 가능한지 확인함
    userValidate.validateAccountCanBeRestored(phoneNumber);
    UserEntity user = UserValidate.getUserByPhoneNumber(phoneNumber, userRepository);
    user.restoreAccount();
    userRepository.save(user);
  }
}
