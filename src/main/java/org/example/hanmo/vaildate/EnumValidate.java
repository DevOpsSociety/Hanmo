package org.example.hanmo.vaildate;

import java.util.Arrays;
import org.example.hanmo.domain.enums.Department;
import org.example.hanmo.domain.enums.Gender;
import org.example.hanmo.domain.enums.Mbti;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.BadRequestException;

public class EnumValidate {

    public static Mbti validateMbti(int code) {
        return Arrays.stream(Mbti.values())
                .filter(mbti -> mbti.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("유효하지 않은 MBTI 코드: " + code, ErrorCode.INVALID_CODE_EXCEPTION));
    }

    public static Department validateDepartment(int code) {
        return Arrays.stream(Department.values())
                .filter(dept -> dept.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("유효하지 않은 학과 코드: " + code, ErrorCode.INVALID_CODE_EXCEPTION));
    }

    public static Gender validateGender(int code) {
        return Arrays.stream(Gender.values())
                .filter(dept -> dept.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("유효하지 않은 성별 코드: " + code, ErrorCode.INVALID_CODE_EXCEPTION));
    }
}
