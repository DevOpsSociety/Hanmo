package org.example.hanmo.dto.Nmo.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class NmoPagedResponseDto {
  @Schema(description = "Nmo 게시글 리스트")
  private List<NmoResponseDto> nmoResponseDtoList;
  @Schema(description = "페이지 번호")
  private int pageNumber;
  @Schema(description = "한 페이지 당 개수")
  private int pageSize;
  @Schema(description = "총 게시글 수")
  private long totalElements;
  @Schema(description = "총 페이지 수")
  private int totalPages;
  @Schema(description = "게시글 리스트 마지막 페이지 여부")
  private boolean last;
}
