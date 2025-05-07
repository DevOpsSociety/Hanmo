package org.example.hanmo.vaildate;

import java.util.Arrays;

import org.example.hanmo.domain.enums.Department;
import org.example.hanmo.domain.enums.Gender;
import org.example.hanmo.domain.enums.Mbti;
import org.example.hanmo.domain.enums.UserRole;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.BadRequestException;

public class EnumValidate {

  public static Mbti validateMbti(Integer code) {
    int nonNullCode = requireNonNullCode(code, "MBTI");
    return Arrays.stream(Mbti.values())
        .filter(mbti -> mbti.getCode() == nonNullCode)
        .findFirst()
        .orElseThrow(
            () ->
                new BadRequestException(
                    "유효하지 않은 MBTI 코드: " + code, ErrorCode.INVALID_CODE_EXCEPTION));
  }

  public static Department validateDepartment(Integer code) {
    int nonNullCode = requireNonNullCode(code, "학과");
    return Arrays.stream(Department.values())
        .filter(dept -> dept.getCode() == nonNullCode)
        .findFirst()
        .orElseThrow(
            () ->
                new BadRequestException(
                    "유효하지 않은 학과 코드: " + code, ErrorCode.INVALID_CODE_EXCEPTION));
  }

  public static Gender validateGender(Integer code) {
    int nonNullCode = requireNonNullCode(code, "성별");
    return Arrays.stream(Gender.values())
        .filter(gender -> gender.getCode() == nonNullCode)
        .findFirst()
        .orElseThrow(
            () ->
                new BadRequestException(
                    "유효하지 않은 성별 코드: " + code, ErrorCode.INVALID_CODE_EXCEPTION));
  }

  public static UserRole validateUserRole(Integer code) {
    int nonNull = requireNonNullCode(code, "사용자 역할");
    return Arrays.stream(UserRole.values())
            .filter(r -> r.getCode() == nonNull)
            .findFirst()
            .orElseThrow(() -> new BadRequestException(
                    "유효하지 않은 UserRole 코드: " + code,
                    ErrorCode.INVALID_CODE_EXCEPTION));
  }

  private static Integer requireNonNullCode(Integer code, String type) {
    if (code == null) {
      throw new BadRequestException(type + " 코드가 비어있습니다.", ErrorCode.INVALID_CODE_EXCEPTION);
    }
    return code;
  }
}
