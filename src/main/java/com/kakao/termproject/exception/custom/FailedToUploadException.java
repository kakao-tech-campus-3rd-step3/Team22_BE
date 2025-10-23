package com.kakao.termproject.exception.custom;

public class FailedToUploadException extends RuntimeException {

  public FailedToUploadException(String message) {
    super(message);
  }
}
