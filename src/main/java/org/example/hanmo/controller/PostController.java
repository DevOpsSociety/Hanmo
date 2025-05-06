package org.example.hanmo.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.example.hanmo.dto.post.request.PostRequestDto;
import org.example.hanmo.dto.post.response.PagedResponseDto;
import org.example.hanmo.dto.post.response.PostResponseDto;
import org.example.hanmo.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  @Operation(summary = "게시글 작성",tags = {"게시물"})
  @PostMapping("/create")
  public ResponseEntity<String> createPost(
      HttpServletRequest request, @RequestBody PostRequestDto postRequestDto) {
    postService.createPost(request, postRequestDto);
    return ResponseEntity.ok("게시글 작성 완료");
  }

  @Operation(summary = "게시글 조회(최신순)",tags = {"게시물"})
  @GetMapping("")
  public PagedResponseDto<PostResponseDto> getPosts(
      HttpServletRequest request,
      @RequestParam(value = "page", required = false, defaultValue = "0")
          @Parameter(description = "페이지 번호", example = "0")
          int page,
      @RequestParam(value = "size", required = false, defaultValue = "5")
          @Parameter(description = "페이지 크기", example = "5")
          int size) {
    Pageable pageable = PageRequest.of(page, size);
    return postService.getPosts(request, pageable);
  }

  @Operation(summary = "게시글 수정",tags = {"게시물"})
  @PutMapping("/{id}")
  public ResponseEntity<String> updatePost(
      @PathVariable("id") @Parameter(description = "게시글 ID", example = "1") Long id, HttpServletRequest request,
      @RequestBody PostRequestDto postRequestDto) {
    postService.updatePost(id, request, postRequestDto);
    return ResponseEntity.ok("게시글 수정 완료");
  }

  @Operation(summary = "게시글 삭제",tags = {"게시물"})
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deletePost(
      @PathVariable("id") @Parameter(description = "게시글 ID") Long id, HttpServletRequest request) {
    postService.deletePost(id, request);
    return ResponseEntity.ok("게시글 삭제 완료");
  }
}
