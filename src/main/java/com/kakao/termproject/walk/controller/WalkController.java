package com.kakao.termproject.walk.controller;

import com.kakao.termproject.exception.ErrorResult;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.walk.dto.WalkData;
import com.kakao.termproject.walk.dto.WalkResponse;
import com.kakao.termproject.walk.dto.WalkSaveResponse;
import com.kakao.termproject.walk.service.WalkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "산책 경로 저장 및 조회")
@RestController
@RequestMapping("/api/walks")
@RequiredArgsConstructor
@Slf4j
public class WalkController {

  private final WalkService walkService;

  @ApiResponse(responseCode = "200", description = "조회 성공")
  @ApiResponse(responseCode = "404", description = "사용자의 주 경로가 없어 조회에 실패한 경우",
    content = @Content(schema = @Schema(implementation = ErrorResult.class),
      examples = @ExampleObject(
        value = "{\n"
          + "    \"status\": \"NOT_FOUND\",\n"
          + "    \"message\": \"해당되는 id의 산책 경로가 존재하지 않습니다.\"\n"
          + "}"
      )))
  @Operation(summary = "조회", description = "현재 로그인되어있는 유저의 주 산책경로를 반환합니다. Authorization 헤더를 추가하여 인증 인가 과정을 거칩니다.")
  @GetMapping
  public ResponseEntity<WalkResponse> getWalk(
    @AuthenticationPrincipal Member member
  ) {
    log.info("get Walk data for member: {}", member);
    return ResponseEntity.ok(walkService.getWalkById(member));
  }

  @ApiResponse(responseCode = "201", description = "사용자의 주 경로가 없는 경우 새로 저장")
  @ApiResponse(responseCode = "200", description = "사용자의 주 경로가 있는 경우 업데이트")
  @Operation(summary = "저장", description = "현재 로그인되어있는 유저의 주 산책경로를 저장합니다. Authorization 헤더를 추가하여 인증 인가 과정을 거칩니다.")
  @PostMapping
  public ResponseEntity<WalkResponse> saveWalk(
    @AuthenticationPrincipal Member member,
    @RequestBody WalkData data) {
    log.info("Walk save Request");
    WalkSaveResponse response = walkService.saveWalk(member, data);
    return ResponseEntity.status(response.status())
      .body(response.walkResponse());
  }
}
