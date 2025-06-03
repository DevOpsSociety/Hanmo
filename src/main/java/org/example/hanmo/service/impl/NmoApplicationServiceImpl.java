package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.NmoApplicationEntity;
import org.example.hanmo.domain.NmoEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.NmoApplication.response.NmoApplicationResponseDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.BadRequestException;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.repository.Nmo.NmoRepository;
import org.example.hanmo.repository.NmoApplication.NmoApplyRepository;
import org.example.hanmo.service.NmoApplicationService;
import org.example.hanmo.service.NmoService;
import org.example.hanmo.vaildate.AuthValidate;
import org.example.hanmo.vaildate.NmoApplyValidate;
import org.example.hanmo.vaildate.NmoValidate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NmoApplicationServiceImpl implements NmoApplicationService {

  private final AuthValidate authValidate;
  private final NmoValidate nmoValidate;
  private final NmoApplyValidate nmoApplyValidate;
  private final NmoApplyRepository nmoApplyRepository;

  @Override
  public void applyToNmo(String token, Long NmoId) {
    UserEntity user = authValidate.validateTempToken(token);
    NmoEntity nmo = nmoValidate.validateExists(NmoId);

    // 이미 신청한 Nmo인지
    nmoApplyValidate.validateNotAlreadyApplied(user.getId(), nmo.getId());
    // 신청자 수가 제한 초과인지 확인
    nmoApplyValidate.validateRecruitmentLimit(nmo.getId(), nmo.getRecruitLimit());

    NmoApplicationEntity nmoApplicationEntity = NmoApplicationEntity.builder()
        .user(user)
        .nmo(nmo)
        .build();

    nmoApplyRepository.save(nmoApplicationEntity);
  }

  @Override
  public void cancelApplication(String token, Long NmoId) {
    UserEntity user = authValidate.validateTempToken(token);

    NmoApplicationEntity application = nmoApplyRepository
        .findByUserIdAndNmoId(user.getId(), NmoId)
        .orElseThrow(() -> new NotFoundException("신청 내역이 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));

    nmoApplyRepository.delete(application);
  }

  @Override
  public List<NmoApplicationResponseDto> getApplicants(String token, Long NmoId) {
    return List.of();
  }
}
