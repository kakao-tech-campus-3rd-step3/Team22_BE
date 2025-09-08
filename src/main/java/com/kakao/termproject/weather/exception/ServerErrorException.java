package com.kakao.termproject.weather.exception;

import org.springframework.http.HttpStatusCode;

public class ServerErrorException extends RuntimeException {

  private final HttpStatusCode statusCode;

  public ServerErrorException(HttpStatusCode statusCode, String message) {
    super("Server error " + statusCode + ": " + message);
    this.statusCode = statusCode;
  }
}
