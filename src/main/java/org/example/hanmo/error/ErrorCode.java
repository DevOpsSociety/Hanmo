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
  SMS_INVALID_CODE_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "유효하지 않은 인증번호입니다."),

  INVALID_CODE_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "유효하지 않은 코드입니다."),
  DUPLICATE_NICKNAME_EXCEPTION(HttpStatus.BAD_REQUEST, "409", "이미 사용중인 닉네임입니다."),
  POST_CONTENT_LENGTH_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "게시글은 최대 35자까지 입력 가능합니다."),

  INSUFFICIENT_USERS_FOR_MATCHING_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "매칭할 유저 수가 충분하지 않습니다."),
  DEPARTMENT_CONFLICT_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "이성 유저 간 학과가 겹칠 수 없습니다."),
  MATCHING_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "404", "매칭할 유저를 찾을 수 없습니다."),
  USER_ALREADY_MATCHED(HttpStatus.CONFLICT, "409", "이미 매칭된 유저입니다."),
  MATCHING_ALREADY_IN_PROGRESS(HttpStatus.CONFLICT, "409", "이미 매칭이 진행 중입니다."),
  MATCHING_TYPE_CONFLICT(HttpStatus.CONFLICT, "409", "이미 1:1/2:2 매칭을 신청한 상태입니다. 다른 타입의 매칭을 신청할 수 없습니다."),
  GENDER_MATCHING_TYPE_CONFLICT(HttpStatus.CONFLICT, "409", "이미 1:1 동성/이성 매칭을 신청한 상태입니다. 다른 타입의 매칭을 신청할 수 없습니다."),
  MATCHING_NOT_IN_PROGRESS(HttpStatus.CONFLICT, "409", "매칭 대기 상태가 아닙니다."),

  ALREADY_DORMANT_ACCOUNT_EXCEPTION(HttpStatus.CONFLICT, "409", "휴면(탈퇴) 상태인 계정입니다."),
  ACCOUNT_NOT_DORMANT_EXCEPTION(HttpStatus.CONFLICT, "409", "해당 계정은 휴면 상태가 아닙니다."),
  REACTIVATION_PERIOD_EXPIRED(HttpStatus.CONFLICT, "409", "복구 가능 기간이 지났습니다. 새로운 회원가입을 진행해주세요."),
  DUPLICATE_ACCOUNT_EXCEPTION(HttpStatus.CONFLICT, "409", "이미 가입된 계정입니다."),
  DUPLICATE_STUDENT_NUMBER_EXCEPTION(HttpStatus.CONFLICT, "409", "이미 사용중인 학번입니다."),
  INVALID_STUDENT_NUMBER_FORMAT(HttpStatus.CONFLICT, "400", "학번 형식에 맞지않습니다."),
  TOO_EARLY_FOR_REMATCHING(HttpStatus.CONFLICT, "K400", "아직 하루가 지나지 않아, 다시 매칭이 불가능합니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  ErrorCode(HttpStatus status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }
}
