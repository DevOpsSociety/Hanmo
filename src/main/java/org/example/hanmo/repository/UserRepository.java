package org.example.hanmo.repository;

import org.example.hanmo.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByNickname(String nickname);

}
