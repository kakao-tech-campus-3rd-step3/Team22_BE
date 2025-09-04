package com.kakao.termproject.exception;

import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.exception.custom.EmailDuplicationException;
import com.kakao.termproject.exception.custom.InvalidPasswordException;
import com.kakao.termproject.exception.custom.JsonParseException;
import com.kakao.termproject.exception.custom.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(value = DataNotFoundException.class)
  public ResponseEntity<ErrorResult> dataNotFound(DataNotFoundException e) {
    return new ResponseEntity<>(
      new ErrorResult(
        HttpStatus.NOT_FOUND,
        e.getMessage(),
        e.getStackTrace()
      ),
      HttpStatus.NOT_FOUND
    );
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(value = JsonParseException.class)
  public ResponseEntity<ErrorResult> jsonProcessingException(JsonParseException e) {
    return new ResponseEntity<>(
      new ErrorResult(
        HttpStatus.BAD_REQUEST,
        e.getMessage(),
        e.getStackTrace()
      ),
      HttpStatus.BAD_REQUEST
    );
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(value = EmailDuplicationException.class)
  public ResponseEntity<ErrorResult> emailDuplicationException(EmailDuplicationException e) {
    return new ResponseEntity<>(
        new ErrorResult(
            HttpStatus.CONFLICT,
            e.getMessage(),
            e.getStackTrace()
        ),
        HttpStatus.CONFLICT
    );
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(value = InvalidPasswordException.class)
  public ResponseEntity<ErrorResult> invalidPasswordException(InvalidPasswordException e) {
    return new ResponseEntity<>(
        new ErrorResult(
            HttpStatus.UNAUTHORIZED,
            e.getMessage(),
            e.getStackTrace()
        ),
        HttpStatus.UNAUTHORIZED
    );
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(value = UserNotFoundException.class)
  public ResponseEntity<ErrorResult> userNotFoundException(UserNotFoundException e) {
    return new ResponseEntity<>(
        new ErrorResult(
            HttpStatus.UNAUTHORIZED,
            e.getMessage(),
            e.getStackTrace()
        ),
        HttpStatus.UNAUTHORIZED
    );
  }
}
