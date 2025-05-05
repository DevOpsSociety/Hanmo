package org.example.hanmo.vaildate;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.domain.enums.WithdrawalStatus;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.*;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.util.RandomNicknameUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserValidate {

  private final UserRepository userRepository;
  private final StringRedisTemplate stringRedisTemplate;

  public static void validateDuplicateNickname(String nickname, UserRepository userRepository) {
    if (StringUtils.isNotBlank(nickname) && userRepository.existsByNickname(nickname)) {
      throw new BadRequestException("이미 사용 중인 닉네임입니다.", ErrorCode.DUPLICATE_NICKNAME_EXCEPTION);
    }
  }

  public static void setUniqueRandomNicknameIfNeeded(
      UserEntity user, boolean regenerate, UserRepository userRepository) {
    if (regenerate || user.getNickname() == null || user.getNickname().trim().isEmpty()) {
      String uniqueNickname =
          java.util.stream.Stream.generate(
                  () -> RandomNicknameUtil.generateNickname(user.getDepartment()))
              .filter(nickname -> !userRepository.existsByNickname(nickname))
              .findFirst()
              .orElseThrow(
                  () ->
                      new BadRequestException(
                          "이미 사용 중인 닉네임입니다.", ErrorCode.DUPLICATE_NICKNAME_EXCEPTION));
      user.setNickname(uniqueNickname);
    }
  }

  public static UserEntity getUserByPhoneNumber(String phoneNumber, UserRepository userRepository) {
    return userRepository
        .findByPhoneNumber(phoneNumber)
        .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));
  }

  public static String validatePhoneNumberByTempToken(
      String tempToken, RedisTempRepository redisTempRepository) {
    String phoneNumber = redisTempRepository.getPhoneNumberByTempToken(tempToken);
    if (phoneNumber == null) {
      throw new ForbiddenException("SMS오류입니다.", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION);
    }
    return phoneNumber;
  }

  public static void validateDuplicateStudentNumber(
      String studentNumber, UserRepository userRepository) {
    if (StringUtils.isNotBlank(studentNumber)
        && userRepository.existsByStudentNumber(studentNumber)) {
      throw new BadRequestException(
          "이미 사용 중인 학번입니다.", ErrorCode.DUPLICATE_STUDENT_NUMBER_EXCEPTION);
    }
  }

  public static void validateStudentNumberFormat(String studentNumber) {
    if (StringUtils.isBlank(studentNumber) || !studentNumber.matches("\\d+")) {
      throw new BadRequestException("학번은 숫자만 포함되어야 합니다.", ErrorCode.INVALID_STUDENT_NUMBER_FORMAT);
    }
  }

  public UserEntity findByPhoneNumberAndStudentNumber(String phoneNumber, String studentNumber) {
    return userRepository
        .findByPhoneNumberAndStudentNumber(phoneNumber, studentNumber)
        .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));
  }

  public static void validateNicknameNotChanged(UserEntity user) {
    if (user.isNicknameChanged()) {
      throw new BadRequestException("이미 닉네임이 변경되었습니다.", ErrorCode.DUPLICATE_NICKNAME_EXCEPTION);
    }
  }

  public static void validateUserIsActive(UserEntity user) {
    if (user.getWithdrawalStatus() == WithdrawalStatus.WITHDRAWN) {
      throw new AccountDeactivatedException(
          "이미 휴면(탈퇴) 상태의 계정입니다.", ErrorCode.ALREADY_DORMANT_ACCOUNT_EXCEPTION);
    }
  }

  // 탈퇴 가능한 상태인지 확인
  public void validateAccountCanBeDeactivated(String phoneNumber) {
    UserEntity user = getUserByPhoneNumber(phoneNumber, userRepository);
    if (user.getWithdrawalStatus() == WithdrawalStatus.WITHDRAWN) {
      throw new AccountDeactivatedException(
          "이미 휴면 상태의 계정입니다.", ErrorCode.ALREADY_DORMANT_ACCOUNT_EXCEPTION);
    }
  }

  // 회원가입전에 이미 탈퇴 한 회원인지,(탈퇴하고 하루동안은 회원가입이 아닌 복구로 들어감)
  public void validateAccountForRegistration(String phoneNumber) {
    Optional<UserEntity> existingUserOpt = userRepository.findByPhoneNumber(phoneNumber);
    if (existingUserOpt.isPresent()) {
      UserEntity existingUser = existingUserOpt.get();
      if (existingUser.getWithdrawalStatus() == WithdrawalStatus.ACTIVE) {
        throw new AccountDeactivatedException(
            "이미 가입된 계정입니다.", ErrorCode.DUPLICATE_ACCOUNT_EXCEPTION);
      } else {
        if (existingUser.getWithdrawalTimestamp() != null
            && existingUser.getWithdrawalTimestamp().isAfter(LocalDateTime.now().minusDays(3))) {
          throw new AccountDeactivatedException(
              "탈퇴 후 3일 이내에는 재가입이 불가능합니다. 계정 복구를 진행해주세요.", ErrorCode.REACTIVATION_PERIOD_EXPIRED);
        }
      }
    }
  }

  // 휴면 상태인 계정을 복구하기 전에, 복구가 가능 한 계정인지, 하루가 지났는지 확인함
  public void validateAccountCanBeRestored(String phoneNumber) {
    UserEntity user = getUserByPhoneNumber(phoneNumber, userRepository);
    if (user.getWithdrawalStatus() != WithdrawalStatus.WITHDRAWN) {
      throw new AccountDeactivatedException(
          "해당 계정은 휴면 상태가 아닙니다.", ErrorCode.ACCOUNT_NOT_DORMANT_EXCEPTION);
    }
    if (user.getWithdrawalTimestamp() == null
        || user.getWithdrawalTimestamp().isBefore(LocalDateTime.now().minusDays(3))) {
      throw new AccountDeactivatedException(
          "복구 가능 기간이 지났습니다. 새로운 회원가입을 진행해주세요.", ErrorCode.REACTIVATION_PERIOD_EXPIRED);
    }
  }

  public void validateMatchingCooldown(Long userId, MatchingType type) {
    String prefix =
        type == MatchingType.ONE_TO_ONE ? "match:cooldown:1to1:" : "match:cooldown:2to2:";
    String key = prefix + userId;

    if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
      throw new MatchingException(
          "아직 하루가 지나지않았습니다. 다시 매칭이 불가능합니다.", ErrorCode.TOO_EARLY_FOR_REMATCHING);
    }
  }
}
