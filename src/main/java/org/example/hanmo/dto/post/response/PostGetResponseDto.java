package org.example.hanmo.dto.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hanmo.domain.UserEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostGetResponseDto {

  @Schema(description = "게시글 작성자 닉네임")
  private String nickName;

  @Schema(description = "게시글 내용")
  private String contents;
}
