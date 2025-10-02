package com.kakao.termproject.walk.dto;

import org.springframework.http.HttpStatus;

public record WalkSaveResponse(
  HttpStatus status,
  WalkResponse walkResponse
) {

}
