package com.kakao.termproject.exception;

import com.kakao.termproject.exception.custom.DataNotFoundException;
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
}
