package org.example.hanmo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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

    @JsonCreator
    public static Gender fromCode(Integer code) {
        return EnumValidate.validateGender(code);
    }

    @JsonValue
    public int toValue() {
        return code;
    }

}