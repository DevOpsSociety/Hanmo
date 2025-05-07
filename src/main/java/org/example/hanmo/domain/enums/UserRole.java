package org.example.hanmo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.example.hanmo.vaildate.EnumValidate;

@Getter
public enum UserRole {

    USER(0, "USER"),
    ADMIN(1, "ADMIN");

    private final int code;
    private final String description;

    UserRole(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonCreator
    public static UserRole fromCode(Integer code) {
        return EnumValidate.validateUserRole(code);
    }

    @JsonValue
    public int toValue() {
        return code;
    }
}
