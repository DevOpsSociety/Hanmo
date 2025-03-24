package org.example.hanmo.dto.sms.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    @Schema(description = "전화번호 등록 여부 (true: 이미 등록됨, false: 미등록)")
    private boolean registered;

    @Schema(description = "응답 메시지", example = "가입되지 않은 번호 또는 이미 가입된 번호입니다.")
    private String message;
}
