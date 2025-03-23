package org.example.hanmo.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponseDto {
    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "학과")
    private String instagramId;
}
