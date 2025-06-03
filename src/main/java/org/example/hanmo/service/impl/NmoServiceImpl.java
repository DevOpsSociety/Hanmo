package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.NmoEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.Nmo.request.NmoRequestDto;
import org.example.hanmo.dto.Nmo.response.NmoDetailDto;
import org.example.hanmo.dto.Nmo.response.NmoPagedResponseDto;
import org.example.hanmo.dto.Nmo.response.NmoResponseDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.redis.RedisNmoApplyRepository;
import org.example.hanmo.repository.Nmo.NmoRepository;
import org.example.hanmo.service.NmoService;
import org.example.hanmo.vaildate.AuthValidate;
import org.example.hanmo.vaildate.NmoValidate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NmoServiceImpl implements NmoService {
  
  private final AuthValidate authValidate;
  private final NmoValidate nmoValidate;
  private final NmoRepository nmoRepository;
  private final RedisNmoApplyRepository redisNmoApplyRepository;


  @Override
  public void createNmo(String token, NmoRequestDto nmoRequestDto) {
    UserEntity user = authValidate.validateTempToken(token);

    NmoEntity nmo = NmoEntity.builder()
        .title(nmoRequestDto.getTitle())
        .content(nmoRequestDto.getContent())
        .recruitLimit(nmoRequestDto.getRecruitLimit())
        .author(user)
        .build();

    nmoRepository.save(nmo);
  }

  @Override
  public void updateNmo(Long nmoId, String token, NmoRequestDto nmoRequestDto) {
    UserEntity user = authValidate.validateTempToken(token);
    NmoEntity nmo = nmoValidate.validateExists(nmoId);
    nmoValidate.validateAuthor(nmo.getAuthor().getId(), user.getId(),"수정");
    nmoValidate.validateRecruitLimitForUpdate(nmoId, nmoRequestDto.getRecruitLimit());

    nmo.update(nmoRequestDto.getTitle(), nmoRequestDto.getContent(), nmoRequestDto.getRecruitLimit());
  }

  @Override
  public void deleteNmo(Long nmoId, String token) {
    UserEntity user = authValidate.validateTempToken(token);
    NmoEntity nmo = nmoValidate.validateExists(nmoId);
    nmoValidate.validateAuthor(nmo.getAuthor().getId(), user.getId(),"삭제");

    nmoRepository.delete(nmo);

    redisNmoApplyRepository.deleteApplyCountKey(nmoId);
  }

  @Override
  @Transactional(readOnly = true)
  public NmoDetailDto findNmoById(Long nmoId, String token) {
    authValidate.validateTempToken(token);
    NmoEntity nmo = nmoValidate.validateExists(nmoId);

    return NmoDetailDto.fromEntity(nmo);
  }

  @Override
  @Transactional(readOnly = true)
  public NmoPagedResponseDto findAllNmos(String token, Long lastId, int size) {
    authValidate.validateTempToken(token);

    List<NmoEntity> nmos = nmoRepository.findNmoListAfterId(lastId, size);

    List<NmoResponseDto> nmoResponseDtos = nmos.stream()
        .map(NmoResponseDto::fromEntity)
        .toList();

    boolean isLast = nmos.size() < size;

    return NmoPagedResponseDto.builder()
        .nmoResponseDtoList(nmoResponseDtos)
        .pageSize(nmos.size())
        .last(isLast)
        .build();
  }

  @Override
  public NmoPagedResponseDto getMyNmos(String token, Long lastId, int size) {
    UserEntity user = authValidate.validateTempToken(token);

    List<NmoEntity> myNmos = nmoRepository.findByAuthorId(user.getId(),lastId, size);

    List<NmoResponseDto> nmoResponseDtos = myNmos.stream()
        .map(NmoResponseDto::fromEntity)
        .toList();

    boolean isLast = myNmos.size() < size;

    return NmoPagedResponseDto.builder()
        .nmoResponseDtoList(nmoResponseDtos)
        .pageSize(myNmos.size())
        .last(isLast)
        .build();
  }
}
