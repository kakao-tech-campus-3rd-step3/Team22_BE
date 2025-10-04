package com.kakao.termproject.user.controller;

import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.user.dto.LoginRequest;
import com.kakao.termproject.user.dto.RegisterRequest;
import com.kakao.termproject.user.dto.TokenResponse;
import com.kakao.termproject.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "회원가입", description = "회원가입하기")
  @PostMapping("/register")
  public ResponseEntity<TokenResponse> register(
      @RequestBody @Valid RegisterRequest request)
  {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(authService.register(request));
  }

  @Operation(summary = "로그인", description = "로그인하기")
  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(
      @RequestBody @Valid LoginRequest request
  ){
    return ResponseEntity.status(HttpStatus.OK)
        .body(authService.login(request));
  }

  @Operation(summary = "로그아웃", description = "로그아웃하기")
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @AuthenticationPrincipal Member member
  ){
    authService.logout(member);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "accessToken 재발급", description = "만료된 액세스토큰을 재발급합니다")
  @PostMapping("/refresh")
  public ResponseEntity<?> reissueAccessToken(@RequestBody String refreshToken){

    return ResponseEntity.status(HttpStatus.OK)
        .body(authService.reissueAccessToken(refreshToken));
  }

}
