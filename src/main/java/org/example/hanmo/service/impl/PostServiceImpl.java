package org.example.hanmo.service.impl;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.example.hanmo.domain.PostEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.post.request.PostRequestDto;
import org.example.hanmo.dto.post.response.PagedResponseDto;
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

    postValidate.validateContentLength(postRequestDto);

    PostEntity post =
        PostEntity.builder().content(postRequestDto.getContent()).userId(user).build();

    postRepository.save(post);
  }

  @Override
  public PagedResponseDto<PostResponseDto> getPosts(HttpServletRequest request, Pageable pageable) {
    String tempToken = request.getHeader("tempToken");
    authValidate.validateTempToken(tempToken);

    Page<PostEntity> posts = postRepository.getLatestPosts(pageable);

    List<PostResponseDto> post =
        posts.getContent().stream().map(PostResponseDto::fromEntity).toList();

    return new PagedResponseDto<>(
        post,
        posts.getNumber(),
        posts.getSize(),
        posts.getTotalElements(),
        posts.getTotalPages(),
        posts.isLast());
  }

  @Override
  public void updatePost(Long id, HttpServletRequest request, PostRequestDto postRequestDto) {
    String tempToken = request.getHeader("tempToken");
    UserEntity user = authValidate.validateTempToken(tempToken);

    PostEntity post = postValidate.validatePost(id, user);
    post.update(postRequestDto);
  }

  @Override
  public void deletePost(Long id, HttpServletRequest request) {
    String tempToken = request.getHeader("tempToken");
    UserEntity user = authValidate.validateTempToken(tempToken);

    PostEntity post = postValidate.validatePost(id, user);
    postRepository.delete(post);
  }
}
