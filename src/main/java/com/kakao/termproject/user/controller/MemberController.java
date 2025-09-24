package com.kakao.termproject.user.controller;

import com.kakao.termproject.pet.dto.PetCreateRequest;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.user.service.MemberService;
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

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/{userId}")
  public ResponseEntity<Void> setPet(
      @AuthenticationPrincipal Member member,
      @RequestBody @Valid PetCreateRequest request
  ){
    memberService.setPet(member.getId(), request);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
