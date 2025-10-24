package com.kakao.termproject.exception;

import org.springframework.http.HttpStatus;

public record ErrorResult(
  HttpStatus status,
  String message
) {

}
