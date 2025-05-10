package org.example.hanmo.dto.matching.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PreferMbtiRequest {

  @Schema(description = "MBTI E or I")
  String eiMbti;
  @Schema(description = "MBTI F or T")
  String ftMbti;

}
