package org.example.hanmo.service.impl;

import jakarta.servlet.http.HttpServletRequest;

import org.example.hanmo.domain.PostEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.post.request.PostRequestDto;
import org.example.hanmo.dto.post.response.PostResponseDto;
import org.example.hanmo.repository.post.PostRepository;
import org.example.hanmo.service.PostService;
import org.example.hanmo.vaildate.AuthValidate;
import org.example.hanmo.vaildate.PostValidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final PostValidate postValidate;
  private final AuthValidate authValidate;

  @Override
  public void createPost(HttpServletRequest request, PostRequestDto postRequestDto) {
    String tempToken = request.getHeader("tempToken");
    UserEntity user = authValidate.validateTempToken(tempToken);

    PostEntity post =
        PostEntity.builder().content(postRequestDto.getContent()).userId(user).build();

    postRepository.save(post);
  }

  @Override
  public Page<PostResponseDto> getPosts(HttpServletRequest request, Pageable pageable) {
    String tempToken = request.getHeader("tempToken");
    authValidate.validateTempToken(tempToken);

    Page<PostEntity> posts = postRepository.getLatestPosts(pageable);
    return posts.map(PostResponseDto::fromEntity);
  }

  @Override
  public void updatePost(Long id, HttpServletRequest request, PostRequestDto postRequestDto) {
    String tempToken = request.getHeader("tempToken");
    authValidate.validateTempToken(tempToken);

    PostEntity post = postValidate.validatePost(id);
    post.update(postRequestDto);
  }

  @Override
  public void deletePost(Long id, HttpServletRequest request) {
    String tempToken = request.getHeader("tempToken");
    authValidate.validateTempToken(tempToken);

    PostEntity post = postValidate.validatePost(id);
    postRepository.delete(post);
  }
}
