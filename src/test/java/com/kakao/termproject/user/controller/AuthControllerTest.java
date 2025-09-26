package com.kakao.termproject.user.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.kakao.termproject.user.dto.LoginRequest;
import com.kakao.termproject.user.dto.RegisterRequest;
import com.kakao.termproject.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

  @LocalServerPort
  private int port;

  @MockitoBean
  AuthService authService;

  @Autowired
  private RestClient.Builder builder;

  private RestClient client;

  @Autowired
  private AuthController authController;

  @BeforeEach
  void setUp() {
    client = builder.build();

    RegisterRequest request = new RegisterRequest("test1@gmail.com", "test1", "testpwd1234");
    authService.register(request);

  }

  @Test
  @DisplayName("회원가입 실패 : 이메일 형식 아님")
  void 잘못된_이메일_회원가입_400반환() throws Exception {
    RegisterRequest registerRequest = new RegisterRequest("testgmail.com", "testuser", "testpwd1234");

    var url = "http://localhost:" + port + "/api/register";

    assertThatThrownBy(() -> {
      client.post()
          .uri(url)
          .contentType(MediaType.APPLICATION_JSON)
          .body(registerRequest)
          .retrieve()
          .toEntity(String.class);
    }).isInstanceOf(HttpClientErrorException.BadRequest.class);


  }

  @Test
  void 정상_로그인_200반환() {
    LoginRequest loginRequest = new LoginRequest("test1@gmail.com", "testpwd1234");

    ResponseEntity<String> response = authController.login(loginRequest);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }
}