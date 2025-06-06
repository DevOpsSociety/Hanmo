package org.example.hanmo.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCode {
  // 1. 클라이언트 요청 오류
  BAD_REQUEST_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "400 Bad Request"),
  PARAMETER_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "잘못된 파라미터 값"),

  // 2. 인증/인가 오류
  UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "401", "UnAuthorized User"),
  FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "403", "403 Forbidden"),

  // 3. 리소스 없음
  NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "404", "404 Not Found"),

  // 4. 서버 내부 오류
  INTERNAL_SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "500", "500 Server Error"),

  // 5. 중복 및 충돌 오류
  DUPLICATE_PHONE_NUMBER_EXCEPTION(HttpStatus.CONFLICT, "409", "이미 가입된 번호입니다."),
  DUPLICATE_NICKNAME_EXCEPTION(HttpStatus.BAD_REQUEST, "409", "이미 사용중인 닉네임입니다."),
  POST_CONTENT_LENGTH_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "게시글은 최대 35자까지 입력 가능합니다."),

  // 6. SMS/인증 오류
  SMS_VERIFICATION_FAILED_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "인증번호가 일치하지 않습니다."),
  SMS_CODE_EXPIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "인증번호가 만료되었습니다."),
  SMS_INVALID_CODE_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "유효하지 않은 인증번호입니다."),

  SMS_SENDER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "500", "500 SMS Sender Error"),

  // 7. 기타 클라이언트 오류
  INVALID_CODE_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "유효하지 않은 코드입니다."),

  // 8. 매칭 관련 오류
  INSUFFICIENT_USERS_FOR_MATCHING_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "매칭할 유저 수가 충분하지 않습니다."),
  DEPARTMENT_CONFLICT_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "이성 유저 간 학과가 겹칠 수 없습니다."),
  MATCHING_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "404", "매칭할 유저를 찾을 수 없습니다."),
  USER_ALREADY_MATCHED(HttpStatus.CONFLICT, "409", "이미 매칭된 유저입니다."),
  MATCHING_ALREADY_IN_PROGRESS(HttpStatus.CONFLICT, "409", "이미 매칭이 진행 중입니다."),
  MATCHING_TYPE_CONFLICT(HttpStatus.CONFLICT, "409", "이미 1:1/2:2 매칭을 신청한 상태입니다. 다른 타입의 매칭을 신청할 수 없습니다."),
  GENDER_MATCHING_TYPE_CONFLICT(HttpStatus.CONFLICT, "409", "이미 1:1 동성/이성 매칭을 신청한 상태입니다. 다른 타입의 매칭을 신청할 수 없습니다."),
  MATCHING_NOT_IN_PROGRESS(HttpStatus.CONFLICT, "409", "매칭 대기 상태가 아닙니다."),
  TOO_EARLY_FOR_REMATCHING(HttpStatus.CONFLICT, "K400", "아직 하루가 지나지 않아, 다시 매칭이 불가능합니다."),

  // 9. 계정 상태 및 복구 오류
  ALREADY_DORMANT_ACCOUNT_EXCEPTION(HttpStatus.CONFLICT, "409", "휴면(탈퇴) 상태인 계정입니다."),
  ACCOUNT_NOT_DORMANT_EXCEPTION(HttpStatus.CONFLICT, "409", "해당 계정은 휴면 상태가 아닙니다."),
  REACTIVATION_PERIOD_EXPIRED(HttpStatus.CONFLICT, "409", "복구 가능 기간이 지났습니다. 새로운 회원가입을 진행해주세요."),
  DUPLICATE_ACCOUNT_EXCEPTION(HttpStatus.CONFLICT, "409", "이미 가입된 계정입니다."),
  DUPLICATE_STUDENT_NUMBER_EXCEPTION(HttpStatus.CONFLICT, "409", "이미 사용중인 학번입니다."),
  INVALID_STUDENT_NUMBER_FORMAT(HttpStatus.CONFLICT, "400", "학번 형식에 맞지않습니다."),
  INVALID_PASSWORD_EXCEPTION(HttpStatus.CONFLICT, "P404", "비밀번호를 찾을 수 없습니다."),
  ADMIN_AUTH_REQUIRED(HttpStatus.ACCEPTED, "202", "관리자 추가 인증이 필요합니다."),

  // 10. 채팅방 관련 오류
  CHAT_ROOM_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C500", "채팅방 생성에 실패했습니다."),
  CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "C404", "채팅방을 찾을 수 없습니다."),
  CHAT_ROOM_EXPIRED(HttpStatus.GONE, "C410", "채팅방이 만료되었습니다."),
  CHAT_MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C500", "메시지 전송에 실패했습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "500 Internal Server Error"),

  // Nmo 게시글 관련 오류
  DUPLICATE_NMO_APPLICATION_EXCEPTION(HttpStatus.BAD_REQUEST, "409", "이미 신청한 Nmo 입니다."),
  RECRUITMENT_CLOSED_EXCEPTION(HttpStatus.BAD_REQUEST, "409", "이미 선착순 마감되었습니다."),
  RECRUIT_LIMIT_TOO_SMALL_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "현재 신청자 수보다 적은 인원으로 모집할 수 없습니다."),
  CANNOT_APPLY_OWN_NMO(HttpStatus.BAD_REQUEST, "409", "본인이 작성한 모집글에는 신청할 수 없습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  ErrorCode(HttpStatus status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }
}
