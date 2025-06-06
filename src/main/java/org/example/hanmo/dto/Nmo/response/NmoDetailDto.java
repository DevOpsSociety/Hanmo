package org.example.hanmo.dto.Nmo.response;

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
public class NmoDetailDto {

  @Schema(description = "Nmo 제목")
  private String title;
  @Schema(description = "Nmo 작성자 닉네임")
  private String nickName;
  @Schema(description = "Nmo 내용")
  private String content;
  @Schema(description = "작성 시간")
  private LocalDateTime createDate;
  @Schema(description = "현재 신청 인원")
  private int currentApplicantCount;
  @Schema(description = "Nmo 모집 인원")
  private int recruitLimit;



  public static NmoDetailDto fromEntity(NmoEntity nmo) {
    return NmoDetailDto.builder()
        .title(nmo.getTitle())
        .nickName(nmo.getAuthor().getNickname())
        .content(nmo.getContent())
        .createDate(nmo.getCreateDate())
        .recruitLimit(nmo.getRecruitLimit())
        .currentApplicantCount(nmo.getApplication().size())
        .build();
  }

}
