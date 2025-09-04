package com.kakao.termproject.common;

import org.springframework.http.HttpStatus;

public record ErrorResult(HttpStatus status, String message) {

}
