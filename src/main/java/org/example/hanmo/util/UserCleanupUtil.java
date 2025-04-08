package org.example.hanmo.util;

import java.time.LocalDateTime;
import java.util.List;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCleanupUtil {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredUsers() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusWeeks(1);
        List<UserEntity> expiredUsers = userRepository.findByCreateDateBefore(cutoffDate);
        if (!expiredUsers.isEmpty()) {
            userRepository.deleteAll(expiredUsers);
            System.out.println(
                    "삭제된 사용자 수: " + expiredUsers.size() + "건, 삭제 시각: " + LocalDateTime.now());
        }
    }
}
