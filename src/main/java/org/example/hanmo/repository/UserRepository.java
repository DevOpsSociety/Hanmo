package org.example.hanmo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.MatchingType;
import org.example.hanmo.domain.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByNickname(String nickname);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    Optional<UserEntity> findByPhoneNumberAndStudentNumber(
            String phoneNumber, String studentNumber);

    List<UserEntity> findByCreateDateBefore(LocalDateTime cutoffDate);

    Optional<UserEntity> findByIdAndUserStatus(Long id, UserStatus status);

    @Query("SELECT u FROM UserEntity u WHERE u.matchingType = :matchingType")
    List<UserEntity> findAllByMatchingType(@Param("matchingType") MatchingType matchingType);
}
