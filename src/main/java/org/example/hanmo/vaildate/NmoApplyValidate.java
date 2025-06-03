package org.example.hanmo.vaildate;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.NmoApplyException;
import org.example.hanmo.redis.RedisNmoApplyRepository;
import org.example.hanmo.repository.NmoApply.NmoApplyRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NmoApplyValidate {

  private final NmoApplyRepository nmoApplyRepository;
  private final RedisNmoApplyRepository redisNmoApplyRepository;

  public void validateNotAlreadyApplied(Long userId, Long nmoId) {
    if (nmoApplyRepository.existsByUserIdAndNmoId(userId, nmoId)) {
      throw new NmoApplyException("이미 신청한 Nmo 입니다.", ErrorCode.DUPLICATE_NMO_APPLICATION_EXCEPTION);
    }
  }

  public void validateRecruitmentLimit(Long nmoId, int recruitLimit) {
    int currentApplicantCount = redisNmoApplyRepository.getApplyCount(nmoId);
    if (currentApplicantCount >= recruitLimit) {
      throw new NmoApplyException("이미 선착순 마감되었습니다.", ErrorCode.RECRUITMENT_CLOSED_EXCEPTION);
    }
  }

  public void validateNotAuthor(Long userId, Long authorId) {
    if (userId.equals(authorId)) {
      throw new NmoApplyException("본인이 작성한 모집글에는 신청할 수 없습니다.", ErrorCode.CANNOT_APPLY_OWN_NMO);
    }
  }





}
