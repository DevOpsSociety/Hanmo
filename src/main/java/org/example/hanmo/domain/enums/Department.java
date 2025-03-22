package org.example.hanmo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.example.hanmo.vaildate.EnumValidate;

@Getter
public enum Department {
    SINHAK(1, "신학과"),
    MEDIA(2, "미디어영상광고학과"),
    MANAGEMENT(3, "경영학과"),
    POLICE(4, "경찰행정학과"),
    TOURISM(5, "국제관광학과"),
    ENGLISH(6, "영어학과"),
    CHINESE(7, "중국어학과"),
    COMPUTER(8, "컴퓨터공학과"),
    SECURITY(9, "융합보안학과"),
    NURSING(10, "간호학과"),
    SOCIAL_WELFARE(11, "사회복지학과"),
    MUSIC(12, "음악학과"),
    PERFORMING_ARTS(13, "공연예술학과"),
    VISUAL_DESIGN(14, "시각정보디자인학과"),
    INTERIOR_DESIGN(15, "실내건축디자인학과"),
    FASHION_DESIGN(16, "섬유패션디자인학과"),
    FREE_MAJOR(17, "자유전공학부");

    private final int code;
    private final String departmentType;

    Department(int code, String departmentType) {
        this.code = code;
        this.departmentType = departmentType;
    }

    @JsonCreator
    public static Department fromCode(Integer code) {
        return EnumValidate.validateDepartment(code);
    }

    @JsonValue
    public int toValue() {
        return code;
    }

}
