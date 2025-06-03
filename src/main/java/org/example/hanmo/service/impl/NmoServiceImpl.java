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
  public void updateNmo(Long NmoId, String token, NmoRequestDto nmoRequestDto) {
    UserEntity user = authValidate.validateTempToken(token);
    NmoEntity nmo = nmoValidate.validateExists(NmoId);
    nmoValidate.validateAuthor(nmo,user,"수정");

    nmo.update(nmoRequestDto.getTitle(), nmoRequestDto.getContent(), nmoRequestDto.getRecruitLimit());
  }

  @Override
  public void deleteNmo(Long NmoId, String token) {
    UserEntity user = authValidate.validateTempToken(token);
    NmoEntity nmo = nmoValidate.validateExists(NmoId);
    nmoValidate.validateAuthor(nmo,user,"삭제");

    nmoRepository.delete(nmo);
  }

  @Override
  @Transactional(readOnly = true)
  public NmoDetailDto findNmoById(Long id, String token) {
    // 1. 토큰으로 유저 인증
    authValidate.validateTempToken(token);

    // 2. Nmo 게시글 조회
    NmoEntity nmo = nmoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Nmo 게시글을 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));

    // 3. NmoDetailDto로 변환해서 반환
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
}
