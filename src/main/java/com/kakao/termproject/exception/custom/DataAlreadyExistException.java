package com.kakao.termproject.exception.custom;

public class DataAlreadyExistException extends RuntimeException {

  public DataAlreadyExistException(String message) {
    super(message);
  }
}
