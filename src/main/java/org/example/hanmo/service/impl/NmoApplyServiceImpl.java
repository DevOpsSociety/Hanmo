package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.NmoApplyEntity;
import org.example.hanmo.domain.NmoEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.NmoApply.response.NmoApplyResponseDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.redis.RedisNmoApplyRepository;
import org.example.hanmo.repository.NmoApply.NmoApplyRepository;
import org.example.hanmo.service.NmoApplyService;
import org.example.hanmo.vaildate.AuthValidate;
import org.example.hanmo.vaildate.NmoApplyValidate;
import org.example.hanmo.vaildate.NmoValidate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NmoApplyServiceImpl implements NmoApplyService {

  private final AuthValidate authValidate;
  private final NmoValidate nmoValidate;
  private final NmoApplyValidate nmoApplyValidate;
  private final NmoApplyRepository nmoApplyRepository;
  private final RedisNmoApplyRepository redisNmoApplyRepository;

  @Override
  public void applyToNmo(String token, Long nmoId) {
    UserEntity user = authValidate.validateTempToken(token);
    NmoEntity nmo = nmoValidate.validateExists(nmoId);

    // 본인이 작성한 모집글인지 검증
    nmoApplyValidate.validateNotAuthor(user.getId(), nmo.getAuthor().getId());
    // 이미 신청한 Nmo인지
    nmoApplyValidate.validateNotAlreadyApplied(user.getId(), nmo.getId());
    // 신청자 수가 제한 초과인지 확인
    nmoApplyValidate.validateRecruitmentLimit(nmo.getId(), nmo.getRecruitLimit());

    NmoApplyEntity nmoApplyEntity = NmoApplyEntity.builder()
        .user(user)
        .nmo(nmo)
        .build();

    nmoApplyRepository.save(nmoApplyEntity);

    // Redis에서 applyCount 키 1 증가
    redisNmoApplyRepository.incrementApplyCount(nmo.getId());
  }

  @Override
  public void cancelApplication(String token, Long nmoId) {
    UserEntity user = authValidate.validateTempToken(token);

    NmoApplyEntity application = nmoApplyRepository
        .findByUserIdAndNmoId(user.getId(), nmoId)
        .orElseThrow(() -> new NotFoundException("신청 내역이 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));

    nmoApplyRepository.delete(application);

    // Redis에서 신청자 수 1 감소
    redisNmoApplyRepository.decrementApplyCount(nmoId);
  }

  @Override
  public List<NmoApplyResponseDto> getApplicants(String token, Long nmoId) {
    UserEntity user = authValidate.validateTempToken(token);
    NmoEntity nmo = nmoValidate.validateExists(nmoId);
    nmoValidate.validateAuthor(nmo.getAuthor().getId(), user.getId(), "조회(신청자)");

    List<NmoApplyEntity> applications = nmoApplyRepository.findAllByNmoId(nmoId);

    return applications.stream()
        .map(app -> NmoApplyResponseDto.builder()
            .nickname(app.getUser().getNickname())
            .gender(app.getUser().getGender())
            .build())
        .collect(Collectors.toList());
  }
}
