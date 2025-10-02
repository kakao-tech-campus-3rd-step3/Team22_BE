package com.kakao.termproject.walk.controller;

import com.kakao.termproject.walk.dto.WalkData;
import com.kakao.termproject.walk.dto.WalkResponse;
import com.kakao.termproject.walk.service.WalkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @Operation(summary = "조회", description = "현재 로그인되어있는 유저의 주 산책경로를 반환합니다.")
  @GetMapping("/{walkId}")
  public ResponseEntity<WalkResponse> getWalk(@PathVariable Long walkId) {
    log.info("get Walk data for walkId: {}", walkId);
    return ResponseEntity.ok(walkService.getWalkById(walkId));
  }

  @Operation(summary = "저장", description = "현재 로그인되어있는 유저의 주 산책경로를 저장합니다.")
  @PostMapping
  public ResponseEntity<Long> saveWalk(@RequestBody WalkData data) {
    log.info("Walk save Request");
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(walkService.saveWalk(data));
  }
}
