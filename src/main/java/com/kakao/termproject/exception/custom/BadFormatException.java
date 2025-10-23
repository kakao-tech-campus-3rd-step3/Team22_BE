package com.kakao.termproject.exception.custom;

public class BadFormatException extends RuntimeException {

  public BadFormatException() {
    super("잘못된 형식의 파일입니다.");
  }
}
