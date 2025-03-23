package org.example.hanmo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.domain.PostEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.post.request.PostRequestDto;
import org.example.hanmo.dto.post.response.PostGetResponseDto;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.repository.PostRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.PostService;
import org.example.hanmo.vaildate.AuthValidate;
import org.example.hanmo.vaildate.PostValidate;
import org.example.hanmo.vaildate.UserValidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final RedisTempRepository redisTempRepository;
  private final PostValidate postValidate;

  @Override
  public void createPost(PostRequestDto postRequestDto, String token) {
    String phoneNumber = UserValidate.validatePhoneNumberByTempToken(token, redisTempRepository);
    UserEntity user = UserValidate.getUserByPhoneNumber(phoneNumber, userRepository);

    PostEntity post = PostEntity.builder()
        .content(postRequestDto.getContents())
        .userId(user)
        .build();

    postRepository.save(post);

  }


  @Override
  public Page<PostGetResponseDto> getPosts(String token, Pageable pageable) {
    postValidate.validateTempToken(token);

    Page<PostEntity> posts = postRepository.findAllByOrderByCreateDateDesc(pageable);
    return posts.map(PostGetResponseDto::fromEntity);
  }

  @Override
  public void updatePost(Long id, PostRequestDto postRequestDto, String token) {

  }

  @Override
  public void deletePost(Long id, String token) {

  }
}
