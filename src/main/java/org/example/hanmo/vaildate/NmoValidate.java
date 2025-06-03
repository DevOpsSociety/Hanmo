package org.example.hanmo.vaildate;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.NmoEntity;
import org.example.hanmo.domain.PostEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.error.exception.UnAuthorizedException;
import org.example.hanmo.repository.Nmo.NmoRepository;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NmoValidate {

  private final NmoRepository nmoRepository;

  public NmoEntity validateExists(Long id) {
    return nmoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Nmo 게시글을 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));
  }

  public void validateAuthor(NmoEntity nmo, Long userId, String action) {
    if (!nmo.getAuthor().getId().equals(userId)) {
      throw new UnAuthorizedException("이 Nmo를 " + action + "할 권한이 없습니다.", ErrorCode.UNAUTHORIZED_EXCEPTION);
    }
  }

}
