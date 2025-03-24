package org.example.hanmo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.hanmo.dto.post.request.PostRequestDto;
import org.example.hanmo.dto.post.response.PostGetResponseDto;
import org.example.hanmo.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  @Operation(summary = "게시글 작성")
  @PostMapping("/create")
  public ResponseEntity<String> createPost(HttpServletRequest request,@RequestBody PostRequestDto postRequestDto) {
    postService.createPost(request, postRequestDto);
    return ResponseEntity.ok("게시글 작성 완료");
  }

  @Operation(summary = "게시글 조회(최신순)")
  @GetMapping("")
  public Page<PostGetResponseDto> getPosts(HttpServletRequest request,
                                           @Parameter(description = "페이지 번호", required = false, example = "0") int page,
                                           @Parameter(description = "페이지 크기", required = false, example = "5") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return postService.getPosts(request, pageable);
  }


  @Operation(summary = "게시글 수정")
  @PutMapping("/{id}")
  public ResponseEntity<String> updatePost(@PathVariable Long id, HttpServletRequest request, @RequestBody PostRequestDto postRequestDto) {
    postService.updatePost(id, request , postRequestDto);
    return ResponseEntity.ok("게시글 수정 완료");
  }

  @Operation(summary = "게시글 삭제")
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deletePost(@PathVariable Long id, HttpServletRequest request) {
    postService.deletePost(id, request);
    return ResponseEntity.ok("게시글 삭제 완료");
  }



}
