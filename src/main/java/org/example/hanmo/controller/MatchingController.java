package org.example.hanmo.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.matching.request.RedisUserDto;
import org.example.hanmo.dto.matching.response.MatchingResponse;
import org.example.hanmo.dto.matching.response.MatchingResultResponse;
import org.example.hanmo.service.MatchingService;
import org.example.hanmo.vaildate.AuthValidate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/matching")
@RequiredArgsConstructor
public class MatchingController {
  private final MatchingService matchingService;
  private final AuthValidate authValidate;

  @Operation(summary = "1:1 매칭", description = "동성 유저 간 1:1 매칭을 진행합니다.")
  @PostMapping("/one-to-one")
  public ResponseEntity<MatchingResponse> matchSameGenderOneToOne(
      HttpServletRequest httpServletRequest) {
    String tempToken = httpServletRequest.getHeader("tempToken");
    UserEntity user = authValidate.validateTempToken(tempToken);

    RedisUserDto userDto = user.toRedisUserDto();
    matchingService.waitingOneToOneMatching(userDto);

    MatchingResponse response = matchingService.matchSameGenderOneToOne(tempToken);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "2:2 매칭", description = "이성 유저 간 2:2 매칭을 진행합니다.")
  @PostMapping("/two-to-two")
  public ResponseEntity<MatchingResponse> matchOppositeGenderTwoToTwo(
      HttpServletRequest httpServletRequest) {
    String tempToken = httpServletRequest.getHeader("tempToken");
    UserEntity user = authValidate.validateTempToken(tempToken);

    RedisUserDto userDto = user.toRedisUserDto();
    matchingService.waitingTwoToTwoMatching(userDto);

    MatchingResponse response = matchingService.matchOppositeGenderTwoToTwo(tempToken);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "매칭 결과 조회")
  @GetMapping("/result")
  public ResponseEntity<MatchingResultResponse> getMatchingResult(
      HttpServletRequest httpServletRequest) {
    String tempToken = httpServletRequest.getHeader("tempToken");
    MatchingResultResponse response = matchingService.getMatchingResult(tempToken);

    return ResponseEntity.ok(response);
  }

  @Operation(summary = "매칭 취소", description = "매칭 대기 중인 사용자가 매칭을 취소합니다.")
  @DeleteMapping("/cancel")
  public ResponseEntity<String> cancelMatching(HttpServletRequest httpServletRequest) {
    String tempToken = httpServletRequest.getHeader("tempToken");
    matchingService.cancelMatching(tempToken);
    return ResponseEntity.ok("매칭 신청이 취소되었습니다.");
  }
}
