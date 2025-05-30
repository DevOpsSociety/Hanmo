package org.example.hanmo.dto.Nmo.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class NmoResponseDto {

  @Schema(description = "Nmo 게시글 id")
  private Long id;

  @Schema(description = "Nmo 작성자 닉네임")
  private String nickName;

  @Schema(description = "Nmo 제목")
  private String title;

  @Schema(description = "Nmo 내용")
  private String content;

  @Schema(description = "작성 시간")
  private LocalDateTime createDate;

}
