package org.example.hanmo.dto.post.response;

import org.example.hanmo.domain.PostEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostGetResponseDto {

    @Schema(description = "게시글 작성자 닉네임")
    private String nickName;

    @Schema(description = "게시글 내용")
    private String content;

    public static PostGetResponseDto fromEntity(PostEntity post) {
        return new PostGetResponseDto(post.getUserId().getNickname(), post.getContent());
    }
}
