package org.example.hanmo.domain.enums;

import lombok.Getter;
import org.example.hanmo.vaildate.EnumValidate;

@Getter
public enum Gender {
    M(1,"남"),
    F(2,"여");

    private final int code;
    private final String genderType;

    Gender(int code, String genderType) {
        this.code = code;
        this.genderType = genderType;
    }

    public static Gender fromValidatedCode(int code) {
        return EnumValidate.validateGender(code);

    }
}