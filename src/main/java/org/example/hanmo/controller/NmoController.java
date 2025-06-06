package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.Nmo.request.NmoRequestDto;
import org.example.hanmo.dto.Nmo.response.NmoDetailDto;
import org.example.hanmo.dto.Nmo.response.NmoPagedResponseDto;
import org.example.hanmo.service.NmoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Nmos")
public class NmoController {
  
  private final NmoService nmoService;

  @PostMapping
  @Operation(summary = "Nmo 게시글 작성", tags = {"Nmo"})
  public ResponseEntity<String> createNmo(HttpServletRequest request,
                                  @RequestBody NmoRequestDto nmoRequestDto) {
    String token = request.getHeader("tempToken");
    nmoService.createNmo(token, nmoRequestDto);
    return ResponseEntity.ok("Nmo 게시글 생성 완료");
  }

  @PutMapping("/{id}")
  @Operation(summary = "Nmo 게시글 수정", tags = {"Nmo"})
  public ResponseEntity<String> updateNmo(@PathVariable Long id,
                        HttpServletRequest request,
                        @RequestBody NmoRequestDto nmoRequestDto) {
    String token = request.getHeader("tempToken");
    nmoService.updateNmo(id, token, nmoRequestDto);
    return ResponseEntity.ok("Nmo 게시글 수정 완료");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Nmo 게시글 삭제", tags = {"Nmo"})
  public ResponseEntity<String> deleteNmo(@PathVariable Long id,
                        HttpServletRequest request) {
    String token = request.getHeader("tempToken");
    nmoService.deleteNmo(id, token);
    return ResponseEntity.ok("Nmo 게시글 삭제 완료");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Nmo 게시글 상세조회", tags = {"Nmo"})
  public NmoDetailDto findNmoById(@PathVariable Long id,
                                  HttpServletRequest request) {
    String token = request.getHeader("tempToken");

    return nmoService.findNmoById(id, token);
  }

  @GetMapping("/all")
  @Operation(summary = "Nmo 게시글 전체 조회", tags = {"Nmo"})
  public NmoPagedResponseDto findAllNmos(HttpServletRequest request,
                                         @RequestParam(required = false) Long lastId,
                                         @RequestParam(defaultValue = "10") int size) {
    String token = request.getHeader("tempToken");
    return nmoService.findAllNmos(token, lastId, size);
  }

  @GetMapping("/my")
  @Operation(summary = "자신이 쓴 Nmo 조회", tags = {"Nmo"})
  public NmoPagedResponseDto getMyNmos(HttpServletRequest request,
                                       @RequestParam(required = false) Long lastId,
                                       @RequestParam(defaultValue = "10") int size) {
    String token = request.getHeader("tempToken");
    return nmoService.getMyNmos(token, lastId, size);
  }

}
