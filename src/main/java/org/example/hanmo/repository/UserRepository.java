package org.example.hanmo.repository;

import org.example.hanmo.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByNickname(String nickname);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByPhoneNumberAndStudentNumber(String phoneNumber, String studentNumber);
}
