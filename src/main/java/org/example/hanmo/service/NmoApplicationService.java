package org.example.hanmo.service;

import org.example.hanmo.dto.NmoApplication.response.NmoApplicationResponseDto;

import java.util.List;

public interface NmoApplicationService {
  // 모집 신청(모집인원이 다 찼는데 신청하면 에러 발생)
  void applyToNmo(String token, Long NmoId);
  // 모집 취소
  void cancelApplication(String token, Long NmoId);
  // 신청 조회(자기가 쓴 Nmo게시글에 신청자 조회 보여주는 데이터는 닉네임 성별)
  List<NmoApplicationResponseDto> getApplicants(String token, Long NmoId);

}
