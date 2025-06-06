package org.example.hanmo.vaildate;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.NmoEntity;
import org.example.hanmo.domain.PostEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.BadRequestException;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.error.exception.UnAuthorizedException;
import org.example.hanmo.redis.RedisNmoApplyRepository;
import org.example.hanmo.repository.Nmo.NmoRepository;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NmoValidate {

  private final NmoRepository nmoRepository;
  private final RedisNmoApplyRepository redisNmoApplyRepository;

  public NmoEntity validateExists(Long id) {
    return nmoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Nmo 게시글을 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));
  }

  public void validateAuthor(Long authorId, Long userId, String action) {
    if (!authorId.equals(userId)) {
      throw new UnAuthorizedException("이 Nmo의 " + action + "할 권한이 없습니다.", ErrorCode.UNAUTHORIZED_EXCEPTION);
    }
  }

  public void validateRecruitLimitForUpdate(Long nmoId, int newRecruitLimit) {
    int currentApplyCount = redisNmoApplyRepository.getApplyCount(nmoId);
    if (newRecruitLimit < currentApplyCount) {
      throw new BadRequestException("현재 신청자 수보다 적은 모집 인원으로 수정할 수 없습니다.", ErrorCode.RECRUIT_LIMIT_TOO_SMALL_EXCEPTION);
    }
  }


}
