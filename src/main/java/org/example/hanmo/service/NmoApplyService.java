package org.example.hanmo.service;

import org.example.hanmo.dto.NmoApply.response.NmoApplyResponseDto;

import java.util.List;

public interface NmoApplyService {
  // 모집 신청(모집인원이 다 찼는데 신청하면 에러 발생)
  void applyToNmo(String token, Long nmoId);
  // 모집 취소
  void cancelApplication(String token, Long nmoId);
  // 신청 조회(자기가 쓴 Nmo게시글에 신청자 조회 데이터는 닉네임 성별)
  List<NmoApplyResponseDto> getApplicants(String token, Long nmoId);

}
