package org.example.hanmo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.hanmo.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByNickname(String nickname);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    Optional<UserEntity> findByPhoneNumberAndStudentNumber(
            String phoneNumber, String studentNumber);

    List<UserEntity> findByCreateDateBefore(LocalDateTime cutoffDate);
}
