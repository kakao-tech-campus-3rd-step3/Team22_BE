package com.kakao.termproject.user.controller;

import com.kakao.termproject.pet.dto.PetCreateRequest;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.user.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 기능")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

  private final MemberService memberService;

  @Operation(summary = "저장", description = "현재 로그인 된 유저의 반려견을 설정합니다")
  @PostMapping("/pet")
  public ResponseEntity<Void> setPet(
      @AuthenticationPrincipal Member member,
      @RequestBody @Valid PetCreateRequest request
  ){
    memberService.setPet(member.getId(), request);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
