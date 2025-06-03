package org.example.hanmo.dto.NmoApplication.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NmoApplicationResponseDto {

  @Schema(description = "신청자 닉네임")
  private String nickname;
  @Schema(description = "신청자 성별")
  private String gender;

}
