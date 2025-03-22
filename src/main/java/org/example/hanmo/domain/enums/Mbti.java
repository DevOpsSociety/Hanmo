package org.example.hanmo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.example.hanmo.vaildate.EnumValidate;

@Getter
public enum Mbti {
    INTJ(1, "INTJ"),
    INTP(2, "INTP"),
    ENTJ(3, "ENTJ"),
    ENTP(4, "ENTP"),
    INFJ(5, "INFJ"),
    INFP(6, "INFP"),
    ENFJ(7, "ENFJ"),
    ENFP(8, "ENFP"),
    ISTJ(9, "ISTJ"),
    ISFJ(10, "ISFJ"),
    ESTJ(11, "ESTJ"),
    ESFJ(12, "ESFJ"),
    ISTP(13, "ISTP"),
    ISFP(14, "ISFP"),
    ESTP(15, "ESTP"),
    ESFP(16, "ESFP");

    private final int code;
    private final String mbtiType;

    Mbti(int code, String mbtiType) {
        this.code = code;
        this.mbtiType = mbtiType;
    }

    @JsonCreator
    public static Mbti fromCode(Integer code) {
        return EnumValidate.validateMbti(code);
    }

    @JsonValue
    public int toValue() {
        return code;
    }
}
