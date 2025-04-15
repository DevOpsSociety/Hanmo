package org.example.hanmo.util;

import java.time.LocalDateTime;
import java.util.List;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.WithdrawalStatus;
import org.example.hanmo.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserCleanupUtil {
  private final UserRepository userRepository;

  @Scheduled(cron = "0 0 0 * * *")
  public void deleteExpiredUsers() {
    LocalDateTime cutoffDate = LocalDateTime.now().minusWeeks(1);
    List<UserEntity> expiredUsers = userRepository.findByCreateDateBefore(cutoffDate);
    if (!expiredUsers.isEmpty()) {
      userRepository.deleteAll(expiredUsers);
      System.out.println("삭제된 사용자 수: " + expiredUsers.size() + "건, 삭제 시각: " + LocalDateTime.now());
    }
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void cleanupWithdrawnAccounts() {
    LocalDateTime cutoff = LocalDateTime.now().minusDays(3);
    List<UserEntity> usersToDelete =
        userRepository.findAllByWithdrawalStatusAndWithdrawalTimestampBefore(
            WithdrawalStatus.WITHDRAWN, cutoff);
    if (usersToDelete != null && !usersToDelete.isEmpty()) {
      usersToDelete.forEach(
          user -> {
            log.info(
                "탈퇴 상태 사용자 [{}]의 탈퇴 시각 [{}]가 기준 [{}] 이전이므로 삭제합니다.",
                user.getPhoneNumber(),
                user.getWithdrawalTimestamp(),
                cutoff);
          });
      userRepository.deleteAll(usersToDelete);
    } else {
      log.info("삭제할 휴면(탈퇴) 사용자가 없습니다. 기준 시각: [{}]", cutoff);
    }
  }
}
