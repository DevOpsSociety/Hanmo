package org.example.hanmo.dto.sms.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequestDto {
    @NotNull(message = "휴대폰 번호를 입력해주세요.")
    private String phoneNumber;
}
