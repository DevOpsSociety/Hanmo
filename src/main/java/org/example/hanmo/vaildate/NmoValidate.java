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

  public NmoEntity validateNmo(Long id, UserEntity user, String action) {
    NmoEntity nmo =
        nmoRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("Nmo 게시글을 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));

    // Nmo 작성자 ID와 매개변수로 받은 유저 ID 비교
    if (!nmo.getAuthor().getId().equals(user.getId())) {
      throw new UnAuthorizedException("이 Nmo를 " + action + "할 권한이 없습니다.", ErrorCode.UNAUTHORIZED_EXCEPTION);
    }

    return nmo;
  }

}
