package org.example.hanmo.service.impl;

import jakarta.servlet.http.HttpServletRequest;

import org.example.hanmo.domain.PostEntity;
import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.post.request.PostRequestDto;
import org.example.hanmo.dto.post.response.PostGetResponseDto;
import org.example.hanmo.repository.PostRepository;
import org.example.hanmo.service.PostService;
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

    @Override
    public void createPost(HttpServletRequest request, PostRequestDto postRequestDto) {
        UserEntity user = postValidate.validateTempToken(request);

        PostEntity post =
                PostEntity.builder().content(postRequestDto.getContent()).userId(user).build();

        postRepository.save(post);
    }

    @Override
    public Page<PostGetResponseDto> getPosts(HttpServletRequest request, Pageable pageable) {
        postValidate.validateTempToken(request);

        Page<PostEntity> posts = postRepository.findAllByOrderByCreateDateDesc(pageable);
        return posts.map(PostGetResponseDto::fromEntity);
    }

    @Override
    public void updatePost(Long id, HttpServletRequest request, PostRequestDto postRequestDto) {
        postValidate.validateTempToken(request);

        PostEntity post = postValidate.validatePost(id);
        post.update(postRequestDto);
    }

    @Override
    public void deletePost(Long id, HttpServletRequest request) {
        postValidate.validateTempToken(request);

        PostEntity post = postValidate.validatePost(id);
        postRepository.delete(post);
    }
}
