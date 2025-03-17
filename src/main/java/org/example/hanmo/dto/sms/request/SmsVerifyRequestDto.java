package org.example.hanmo.dto.sms.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmsVerifyRequestDto {
    @NotNull(message = "전화번호를 한번 더 입력해주세요.")
    private String phoneNumber;

    @NotNull(message = "인증번호를 입력해주세요.")
    private String certificationCode;
}
