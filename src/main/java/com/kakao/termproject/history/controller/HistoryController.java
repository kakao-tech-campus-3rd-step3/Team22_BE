package com.kakao.termproject.history.controller;

import com.kakao.termproject.exception.ErrorResult;
import com.kakao.termproject.history.dto.HistoryResponse;
import com.kakao.termproject.history.dto.PagedQuery;
import com.kakao.termproject.history.service.HistoryService;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.walk.dto.WalkData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "산책 기록 저장 및 조회")
@RestController
@RequestMapping("/api/histories")
@RequiredArgsConstructor
@Slf4j
public class HistoryController {

  private final HistoryService historyService;

  @ApiResponse(responseCode = "201", description = "사용자의 산책 기록 저장")
  @Operation(summary = "저장", description = "현재 로그인되어있는 유저의 산책 기록을 저장합니다. Authorization 헤더를 추가하여 인증 인가 과정을 거칩니다.")
  @PostMapping
  public ResponseEntity<HistoryResponse> saveHistory(
    @AuthenticationPrincipal Member member,
    @RequestBody @Valid WalkData data
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(historyService.saveHistory(data, member));
  }


  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "조회 성공"),
    @ApiResponse(responseCode = "404", description = "해당하는 id의 산책경로가 없어 조회에 실패한 경우",
      content = @Content(schema = @Schema(implementation = ErrorResult.class),
        examples = @ExampleObject(
          value = "{\n"
            + "    \"status\": \"NOT_FOUND\",\n"
            + "    \"message\": \"해당 id를 가진 산책 기록이 존재하지 않습니다.\"\n"
            + "}"
        ))),
    @ApiResponse(responseCode = "403", description = "조회한 산책 경로가 현재 로그인 중인 사용자의 소유가 아닌 경우",
      content = @Content(schema = @Schema(implementation = ErrorResult.class),
        examples = @ExampleObject(
          value = "{\n"
            + "    \"status\": \"FORBIDDEN\",\n"
            + "    \"message\": \"접근 권한이 없습니다.\"\n"
            + "}"
        )))
  })
  @Operation(summary = "조회", description = "현재 로그인되어있는 유저와 산책 기록의 id를 기반으로 산책 기록을 조회하여 반환합니다. Authorization 헤더를 추가하여 인증 인가 과정을 거칩니다.")
  @GetMapping("/{id}")
  public ResponseEntity<HistoryResponse> getHistory(
    @AuthenticationPrincipal Member member,
    @PathVariable Long id
  ) {
    return ResponseEntity.ok(historyService.getHistory(id, member));
  }

  @ApiResponse(responseCode = "200", description = "조회 성공")
  @Operation(summary = "조회", description = "현재 로그인되어있는 유저의 산책 기록을 전부 반환합니다. 페이지네이션 관련 인자를 쿼리파라미터로 넣어 조회할 수 있습니다. Authorization 헤더를 추가하여 인증 인가 과정을 거칩니다.")
  @GetMapping
  public ResponseEntity<Page<HistoryResponse>> getHistoryList(
    @AuthenticationPrincipal Member member,
    PagedQuery pagedQuery
  ) {
    return ResponseEntity.ok(historyService.getAllHistories(member, pagedQuery));
  }
}
