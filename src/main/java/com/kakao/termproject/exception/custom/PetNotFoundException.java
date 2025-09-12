package com.kakao.termproject.exception.custom;

public class PetNotFoundException extends RuntimeException {

  public PetNotFoundException(String message) {
    super(message);
  }
}

