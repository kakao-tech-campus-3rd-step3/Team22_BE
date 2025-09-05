package com.kakao.termproject.user.controller;

import com.kakao.termproject.user.dto.LoginRequest;
import com.kakao.termproject.user.dto.RegisterRequest;
import com.kakao.termproject.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<String> register(
      @RequestBody @Valid RegisterRequest request)
  {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(
      @RequestBody @Valid LoginRequest request
  ){
    return ResponseEntity.status(HttpStatus.OK)
        .body(userService.login(request));
  }
}
