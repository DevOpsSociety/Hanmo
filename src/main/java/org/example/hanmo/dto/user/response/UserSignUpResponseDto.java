package org.example.hanmo.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpResponseDto {

    @Schema(description = "생성된 닉네임")
    private String nickname;

    @Schema(description = "전화번호")
    private String phoneNumber;
}
