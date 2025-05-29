package org.example.hanmo.error;

import org.example.hanmo.error.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestControllerAdvice
public class ErrorExceptionControllerAdvice {

  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<ErrorEntity> exceptionHandler(final BadRequestException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(
            ErrorEntity.builder()
                .errorCode(e.getErrorCode().getCode())
                .errorMessage(e.getMessage())
                .build());
  }

  @ExceptionHandler({AccountDeactivatedException.class})
  public ResponseEntity<ErrorEntity> exceptionHandler(final AccountDeactivatedException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(
            ErrorEntity.builder()
                .errorCode(e.getErrorCode().getCode())
                .errorMessage(e.getMessage())
                .build());
  }

  @ExceptionHandler({UnAuthorizedException.class})
  public ResponseEntity<ErrorEntity> exceptionHandler(final UnAuthorizedException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(
            ErrorEntity.builder()
                .errorCode(e.getErrorCode().getCode())
                .errorMessage(e.getMessage())
                .build());
  }

  @ExceptionHandler({NotFoundException.class})
  public ResponseEntity<ErrorEntity> exceptionHandler(final NotFoundException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(
            ErrorEntity.builder()
                .errorCode(e.getErrorCode().getCode())
                .errorMessage(e.getMessage())
                .build());
  }

  @ExceptionHandler({SmsSendException.class})
  public ResponseEntity<ErrorEntity> exceptionHandler(final SmsSendException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(
            ErrorEntity.builder()
                .errorCode(e.getErrorCode().getCode())
                .errorMessage(e.getMessage())
                .build());
  }

  @ExceptionHandler({ForbiddenException.class})
  public ResponseEntity<ErrorEntity> exceptionHandler(final ForbiddenException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(
            ErrorEntity.builder()
                .errorCode(e.getErrorCode().getCode())
                .errorMessage(e.getMessage())
                .build());
  }

  @ExceptionHandler({MatchingException.class})
  public ResponseEntity<ErrorEntity> exceptionHandler(final MatchingException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(
            ErrorEntity.builder()
                .errorCode(e.getErrorCode().getCode())
                .errorMessage(e.getMessage())
                .build());
  }

  @ExceptionHandler({TempTokenException.class})
  public ResponseEntity<ErrorEntity> exceptionHandler(final TempTokenException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(
            ErrorEntity.builder()
                .errorCode(e.getErrorCode().getCode())
                .errorMessage(e.getMessage())
                .build());
  }

  @ExceptionHandler({AdminLoginRequiredException.class})
  public ResponseEntity<ErrorEntity> exceptionHandler(final AdminLoginRequiredException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus())
            .body(
                    ErrorEntity.builder()
                            .errorCode(e.getErrorCode().getCode())
                            .errorMessage(e.getMessage())
                            .build());
  }

  @ExceptionHandler({ChatServiceException.class})
  public ResponseEntity<ErrorEntity> exceptionHandler(final ChatServiceException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(
            ErrorEntity.builder()
                .errorCode(e.getErrorCode().getCode())
                .errorMessage(e.getMessage())
                .build());
  }
}
