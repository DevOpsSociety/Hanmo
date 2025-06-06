package org.example.hanmo.dto.Nmo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hanmo.domain.NmoEntity;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime createDate;

  @Schema(description = "현재 신청 인원")
  private int currentApplicantCount;

  @Schema(description = "총 모집 인원")
  private int recruitLimit;

  public static NmoResponseDto fromEntity(NmoEntity nmo) {
    return NmoResponseDto.builder()
        .id(nmo.getId())
        .nickName(nmo.getAuthor().getNickname())
        .title(nmo.getTitle())
        .content(nmo.getContent())
        .createDate(nmo.getCreateDate())
        .currentApplicantCount(nmo.getApplication().size())
        .recruitLimit(nmo.getRecruitLimit())
        .build();
  }

}
