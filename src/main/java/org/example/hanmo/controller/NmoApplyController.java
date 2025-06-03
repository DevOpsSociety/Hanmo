package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.NmoApply.response.NmoApplyResponseDto;
import org.example.hanmo.service.NmoApplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/NmoApply")
@RequiredArgsConstructor
public class NmoApplyController {

  private final NmoApplyService nmoApplyService;

  @PostMapping("/{nmoId}")
  @Operation(summary = "Nmo 게시글 신청하기", tags = {"NmoApply"})
  public ResponseEntity<String> applyToNmo(HttpServletRequest request, @PathVariable Long nmoId) {
    String token = request.getHeader("tempToken");
    nmoApplyService.applyToNmo(token, nmoId);
    return ResponseEntity.ok("신청 완료");
  }

  // ✅ Nmo 신청 취소하기
  @DeleteMapping("/{nmoId}")
  @Operation(summary = "Nmo 게시글 신청 취소", tags = {"NmoApply"})
  public ResponseEntity<String> cancelApplication(HttpServletRequest request, @PathVariable Long nmoId) {
    String token = request.getHeader("tempToken");
    nmoApplyService.cancelApplication(token, nmoId);
    return ResponseEntity.ok("신청 취소 완료");
  }

  // ✅ 신청자 목록 조회 (작성자만 가능)
  @GetMapping("/{nmoId}/applicants")
  @Operation(summary = "Nmo 게시글 신청자 보기", tags = {"NmoApply"})
  public ResponseEntity<List<NmoApplyResponseDto>> getApplicants(HttpServletRequest request, @PathVariable Long nmoId) {
    String token = request.getHeader("tempToken");
    List<NmoApplyResponseDto> applicants = nmoApplyService.getApplicants(token, nmoId);
    return ResponseEntity.ok(applicants);
  }



}
