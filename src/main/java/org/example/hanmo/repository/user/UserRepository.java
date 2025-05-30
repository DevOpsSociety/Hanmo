package org.example.hanmo.repository.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.domain.enums.GenderMatchingType;
import org.example.hanmo.domain.enums.UserStatus;
import org.example.hanmo.domain.enums.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {
  boolean existsByPhoneNumber(String phoneNumber);

  boolean existsByNickname(String nickname);

  boolean existsByLoginId(String loginId);

  Optional<UserEntity> findByPhoneNumber(String phoneNumber);

  Optional<UserEntity> findByNickname(String nickname);

  Optional<UserEntity> findByPhoneNumberAndLoginId(String phoneNumber, String loginId);

  Optional<UserEntity> findByPhoneNumberAndStudentNumber(String phoneNumber, String studentNumber);

  List<UserEntity> findByCreateDateBefore(LocalDateTime cutoffDate);

  boolean existsByStudentNumber(String studentNumber);

  // 탈퇴되지않은 계정 조회
  Optional<UserEntity> findByPhoneNumberAndWithdrawalStatus(
      String phoneNumber, WithdrawalStatus withdrawalStatus);

  Optional<UserEntity> findByIdAndUserStatus(Long id, UserStatus status);

  // 탈퇴한 지 3일이 지난 유저는 DB에서 삭제
  List<UserEntity> findAllByWithdrawalStatusAndWithdrawalTimestampBefore(
      WithdrawalStatus status, LocalDateTime cutoff);

  @Query("SELECT u FROM UserEntity u WHERE u.matchingType = :matchingType")
  List<UserEntity> findAllByMatchingType(@Param("matchingType") MatchingType matchingType);

//  List<UserEntity> findAllByUserStatusAndMatchingType(UserStatus userStatus, MatchingType matchingType);

  List<UserEntity> findAllByUserStatusAndMatchingTypeAndGenderMatchingType(UserStatus userStatus, MatchingType matchingType, GenderMatchingType genderMatchingType);

  long countByCreateDateBetween(LocalDateTime fromInclusive, LocalDateTime toExclusive);

  List<UserEntity> findAllByMatchingGroupId(Long matchingGroupId);
}
