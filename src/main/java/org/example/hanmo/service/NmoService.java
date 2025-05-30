package org.example.hanmo.service;

import org.example.hanmo.domain.NmoEntity;
import org.example.hanmo.dto.Nmo.request.NmoRequestDto;
import org.example.hanmo.dto.Nmo.response.NmoDetailDto;
import org.example.hanmo.dto.Nmo.response.NmoPagedResponseDto;
import org.example.hanmo.dto.Nmo.response.NmoResponseDto;

import java.util.List;

public interface NmoService {
  // 엥모 작성
  void createNmo(NmoRequestDto nmoRequestDto);
  // 엥모 수정(모집 인원 수정, 게시글 수정
  void updateNmo(Long id, NmoRequestDto nmoRequestDto);
  // 엥모 삭제
  void deleteNmo(Long id);
  // 엥모 상세 게시글
  NmoDetailDto findNmoById(Long id);
  // 엥모 게시글 리스트
  // 어떻게 보내줄지는 아직 미정
  List<NmoPagedResponseDto> findAllNmos();
}
