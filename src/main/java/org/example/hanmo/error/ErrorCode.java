package org.example.hanmo.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCode {
    BAD_REQUEST_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "400 Bad Request"),
    PARAMETER_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "잘못된 파라미터 값"),
    UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "401", "UnAuthorized User"),
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "403", "403 Forbidden"),
    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "404", "404 Not Found"),
    INTERNAL_SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "500", "500 Server Error"),
    SMS_SENDER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "500", "500 SMS Sender Error"),
    DUPLICATE_PHONE_NUMBER_EXCEPTION(HttpStatus.CONFLICT, "409", "이미 가입된 번호입니다."),
    SMS_VERIFICATION_FAILED_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "인증번호가 일치하지 않습니다."),
    SMS_CODE_EXPIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "인증번호가 만료되었습니다."),
    INVALID_CODE_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "유효하지 않은 코드입니다."),
    DUPLICATE_NICKNAME_EXCEPTION(HttpStatus.BAD_REQUEST, "409", "이미 사용중인 닉네임입니다."),
    POST_CONTENT_LENGTH_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "게시글은 최대 35자까지 입력 가능합니다."),

    INSUFFICIENT_USERS_FOR_MATCHING_EXCEPTION(
            HttpStatus.BAD_REQUEST, "400", "매칭할 유저 수가 충분하지 않습니다."),
    DEPARTMENT_CONFLICT_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "이성 유저 간 학과가 겹칠 수 없습니다."),
    NO_MATCHING_PARTNER_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "매칭 대상이 아직 없습니다."),
    MATCHING_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "404", "매칭할 유저를 찾을 수 없습니다."),
    USER_ALREADY_MATCHED(HttpStatus.CONFLICT, "409", "이미 매칭된 유저입니다."),
    MATCHING_ALREADY_IN_PROGRESS(HttpStatus.CONFLICT, "409", "이미 매칭이 진행 중입니다."),
    MATCHING_TYPE_CONFLICT(
            HttpStatus.CONFLICT, "409", "이미 1:1/2:2 매칭을 신청한 상태입니다. 다른 타입의 매칭을 신청할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
