package com.kakao.termproject.exception.custom;

public class OwnerMismatchException extends RuntimeException {

  public OwnerMismatchException(String message) {
    super(message);
  }
}
