package org.example.hanmo.dto.NmoApply.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hanmo.domain.enums.Gender;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NmoApplyResponseDto {

  @Schema(description = "신청자 닉네임")
  private String nickname;
  @Schema(description = "신청자 성별")
  private Gender gender;

}
