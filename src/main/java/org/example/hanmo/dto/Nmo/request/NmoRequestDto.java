package org.example.hanmo.dto.Nmo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NmoRequestDto {
  @Schema(description = "Nmo 제목")
  private String title;
  @Schema(description = "Nmo 내용")
  private String content;
  @Schema(description = "Nmo 모집인원")
  private int recruitLimit;
}
