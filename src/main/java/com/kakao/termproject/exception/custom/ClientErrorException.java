package com.kakao.termproject.exception.custom;

import org.springframework.http.HttpStatusCode;

public class ClientErrorException extends RuntimeException {

  private final HttpStatusCode statusCode;

  public ClientErrorException(HttpStatusCode statusCode, String message) {
    super("Client error " + statusCode + ": " + message);
    this.statusCode = statusCode;
  }
}