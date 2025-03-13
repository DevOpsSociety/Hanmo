package org.example.hanmo.dto.sms;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequestDto {

    @Schema(description = "휴대폰 번호 입력")
    private String phoneNumber;
}
