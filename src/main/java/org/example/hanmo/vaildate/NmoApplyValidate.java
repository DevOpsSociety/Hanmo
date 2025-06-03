package org.example.hanmo.vaildate;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.BadRequestException;
import org.example.hanmo.error.exception.NmoApplyException;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.repository.NmoApplication.NmoApplyRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NmoApplyValidate {

  private final NmoApplyRepository nmoApplyRepository;

  public void validateNotAlreadyApplied(Long userId, Long nmoId) {
    if (nmoApplyRepository.existsByUserIdAndNmoId(userId, nmoId)) {
      throw new NmoApplyException("이미 신청한 Nmo 입니다.", ErrorCode.DUPLICATE_NMO_APPLICATION_EXCEPTION);
    }
  }

  public void validateRecruitmentLimit(Long nmoId, int recruitLimit) {
    int currentApplicantCount = nmoApplyRepository.countByNmoId(nmoId);
    if (currentApplicantCount >= recruitLimit) {
      throw new NmoApplyException("모집이 마감되었습니다.", ErrorCode.RECRUITMENT_CLOSED_EXCEPTION);
    }
  }



}
