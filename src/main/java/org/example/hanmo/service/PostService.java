package org.example.hanmo.service;

import org.example.hanmo.dto.post.response.PostGetResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {

  // 게시글 작성
  void createPost();

  // 게시글 조회(페이지 네이션)
  // 5개씩 보여주기
  // 조회 필터링 최신순
  List<PostGetResponseDto> getPosts();
  // 게시글 수정

  // 게시글 삭제
}
