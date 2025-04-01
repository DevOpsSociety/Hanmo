package org.example.hanmo.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.matching.request.OneToOneMatchingRequest;
import org.example.hanmo.dto.matching.request.TwoToTwoMatchingRequest;
import org.example.hanmo.dto.matching.response.MatchingResponse;
import org.example.hanmo.dto.user.response.UserProfileResponseDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.UnAuthorizedException;
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
    public ResponseEntity<MatchingResponse> matchSameGenderOneToOne(HttpServletRequest httpServletRequest, @RequestBody OneToOneMatchingRequest request) {
        String tempToken = httpServletRequest.getHeader("tempToken");
        UserEntity user = authValidate.validateTempToken(tempToken);

        if (!user.getId().equals(request.getUserId())) {
            throw new UnAuthorizedException(
                    "토큰 정보와 사용자 정보가 일치하지 않습니다.", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        matchingService.waitingOneToOneMatching(request);
        MatchingResponse response = matchingService.matchSameGenderOneToOne(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "2:2 매칭", description = "이성 유저 간 2:2 매칭을 진행합니다.")
    @PostMapping("/two-to-two")
    public ResponseEntity<MatchingResponse> matchOppositeGenderTwoToTwo(
            HttpServletRequest httpServletRequest, @RequestBody TwoToTwoMatchingRequest request) {
        String tempToken = httpServletRequest.getHeader("tempToken");
        UserEntity user = authValidate.validateTempToken(tempToken);

        if (!user.getId().equals(request.getUserId())) {
            throw new UnAuthorizedException(
                    "토큰 정보와 사용자 정보가 일치하지 않습니다.", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        matchingService.waitingTwoToTwoMatching(request);
        MatchingResponse response = matchingService.matchOppositeGenderTwoToTwo(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "매칭 결과 조회")
    @GetMapping("/result")
    public ResponseEntity<List<UserProfileResponseDto>> getMatchingResult(HttpServletRequest httpServletRequest) {
        String tempToken = httpServletRequest.getHeader("tempToken");
        List<UserProfileResponseDto> response = matchingService.getMatchingResult(tempToken);

        return ResponseEntity.ok(response);
    }
}
