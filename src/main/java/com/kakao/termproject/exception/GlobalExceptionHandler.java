package com.kakao.termproject.exception;

import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.exception.custom.EmailDuplicationException;
import com.kakao.termproject.exception.custom.InvalidPasswordException;
import com.kakao.termproject.exception.custom.JsonParseException;
import com.kakao.termproject.exception.custom.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = DataNotFoundException.class)
  public ResponseEntity<ErrorResult> dataNotFound(DataNotFoundException e) {
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(
        new ErrorResult(
          HttpStatus.NOT_FOUND,
          e.getMessage(),
          e.getStackTrace()
        )
      );
  }

  @ExceptionHandler(value = JsonParseException.class)
  public ResponseEntity<ErrorResult> jsonProcessingException(JsonParseException e) {
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(
        new ErrorResult(
          HttpStatus.BAD_REQUEST,
          e.getMessage(),
          e.getStackTrace()
        )
      );
  }

  @ExceptionHandler(value = EmailDuplicationException.class)
  public ResponseEntity<ErrorResult> emailDuplicationException(EmailDuplicationException e) {
    return ResponseEntity
      .status(HttpStatus.CONFLICT)
      .body(
        new ErrorResult(
          HttpStatus.CONFLICT,
          e.getMessage(),
          e.getStackTrace()
        )
      );
  }

  @ExceptionHandler(value = InvalidPasswordException.class)
  public ResponseEntity<ErrorResult> invalidPasswordException(InvalidPasswordException e) {
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(
        new ErrorResult(
          HttpStatus.UNAUTHORIZED,
          e.getMessage(),
          e.getStackTrace()
        )
      );
  }

  @ExceptionHandler(value = UserNotFoundException.class)
  public ResponseEntity<ErrorResult> userNotFoundException(UserNotFoundException e) {
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(new ErrorResult(
          HttpStatus.UNAUTHORIZED,
          e.getMessage(),
          e.getStackTrace()
        )
      );
  }
}
