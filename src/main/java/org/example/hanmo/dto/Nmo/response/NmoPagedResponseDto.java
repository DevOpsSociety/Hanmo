package org.example.hanmo.dto.Nmo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NmoPagedResponseDto {
  @Schema(description = "Nmo 게시글 리스트")
  private List<NmoResponseDto> nmoResponseDtoList;
  @Schema(description = "한 번에 불러올 개수")
  private int pageSize;
  @Schema(description = "게시글 리스트 마지막 페이지 여부")
  private boolean last;
}
