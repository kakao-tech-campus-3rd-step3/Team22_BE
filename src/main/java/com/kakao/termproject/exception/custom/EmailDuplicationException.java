package com.kakao.termproject.exception.custom;

public class EmailDuplicationException extends RuntimeException {
  public EmailDuplicationException(String message) {
    super(message);
  }
}
