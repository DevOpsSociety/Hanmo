package org.example.hanmo.domain.enums;

import org.example.hanmo.vaildate.EnumValidate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum Department {
  SINHAK(1, "신학"),
  MEDIA(2, "미광"),
  MANAGEMENT(3, "경영"),
  POLICE(4, "경행"),
  TOURISM(5, "국관"),
  ENGLISH(6, "영어학과"),
  CHINESE(7, "중국어학과"),
  COMPUTER(8, "컴공"),
  SECURITY(9, "융보"),
  NURSING(10, "간호"),
  SOCIAL_WELFARE(11, "사복"),
  MUSIC(12, "음악"),
  PERFORMING_ARTS(13, "공예"),
  VISUAL_DESIGN(14, "시디"),
  INTERIOR_DESIGN(15, "실건디"),
  FASHION_DESIGN(16, "섬패디"),
  FREE_MAJOR(17, "자유");

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
