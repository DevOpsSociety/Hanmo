package org.example.hanmo.dto.Nmo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NmoDetailDto {

  @Schema(description = "Nmo 제목")
  private String title;
  @Schema(description = "Nmo 작성자 닉네임")
  private String nickName;
  @Schema(description = "Nmo 내용")
  private String content;
  @Schema(description = "Nmo 모집 인원")
  private int recruitLimit;
  @Schema(description = "작성 시간")
  private LocalDateTime createDate;


}
