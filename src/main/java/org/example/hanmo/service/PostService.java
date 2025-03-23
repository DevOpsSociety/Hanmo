package org.example.hanmo.service;

import org.example.hanmo.dto.post.request.PostRequestDto;
import org.example.hanmo.dto.post.response.PostGetResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {

  // 게시글 작성
  void createPost(PostRequestDto postRequestDto, String token);
  // 게시글 조회(페이지 네이션)
  // 5개씩 보여주기
  // 조회 필터링 최신순
  Page<PostGetResponseDto> getPosts(String token, Pageable pageable);
  // 게시글 수정
  void updatePost(Long id, PostRequestDto postRequestDto, String token);

  // 게시글 삭제
  void deletePost(Long id, String token);
}
