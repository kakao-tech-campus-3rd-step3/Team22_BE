package com.kakao.termproject.user.controller;

import com.kakao.termproject.user.domain.User;
import com.kakao.termproject.user.dto.LoginRequest;
import com.kakao.termproject.user.dto.RegisterRequest;
import com.kakao.termproject.user.jwt.JwtUtil;
import com.kakao.termproject.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;

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
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password())
    );

    User user = (User) authentication.getPrincipal();

    String accessToken = jwtUtil.createAccessToken(user);

    return ResponseEntity.status(HttpStatus.OK)
        .body(accessToken);
  }

}
