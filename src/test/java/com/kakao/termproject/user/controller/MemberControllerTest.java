package com.kakao.termproject.user.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kakao.termproject.pet.domain.Gender;
import com.kakao.termproject.pet.dto.PetCreateRequest;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.user.dto.MemberNameRequest;
import com.kakao.termproject.user.service.MemberService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

  @InjectMocks
  private MemberController memberController;

  @Mock
  private MemberService memberService;

  Member mockMember;

  @BeforeEach
  void setUp() {
     mockMember = mock(Member.class);
  }

  @Test
  @DisplayName("반려견 등록 성공")
  void 반려견_등록_성공(){

    when(mockMember.getId()).thenReturn(1L);


    PetCreateRequest petCreateRequest = new PetCreateRequest(
        "testpet",
        "testbreed",
        Gender.MALE,
        "2020-02-02",
        false,
        true,
        BigDecimal.valueOf(10),
        "sun",
        "heart",
        "asphalt"
    );

    ResponseEntity<Void> response = memberController.setPet(mockMember, petCreateRequest);

    verify(memberService).setPet(1L, petCreateRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("사용자 이름 변경")
  void 사용자_이름_변경_성공(){
    when(mockMember.getId()).thenReturn(1L);

    MemberNameRequest memberNameRequest = new MemberNameRequest(
        "test"
    );

    ResponseEntity<Void> response = memberController.setUsername(mockMember, memberNameRequest);

    verify(memberService).setName(1L, memberNameRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

}