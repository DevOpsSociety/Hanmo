package org.example.hanmo.vaildate;

import org.example.hanmo.domain.PostEntity;
import org.example.hanmo.dto.post.request.PostRequestDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.BadRequestException;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.repository.post.PostRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostValidate {

  private final PostRepository postRepository;

  public PostEntity validatePost(Long id) {
    return postRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));
  }

  public void validateContentLength(PostRequestDto postRequestDto) {
    // 글자 수 검증
    if (postRequestDto.getContent().length() > 35) {
      throw new BadRequestException("최대 글자 수를 넘겼습니다.", ErrorCode.POST_CONTENT_LENGTH_EXCEPTION);
    }
  }
}
