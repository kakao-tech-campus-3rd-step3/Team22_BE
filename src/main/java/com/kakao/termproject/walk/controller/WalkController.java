package com.kakao.termproject.walk.controller;

import com.kakao.termproject.walk.dto.WalkData;
import com.kakao.termproject.walk.service.WalkService;
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

@RestController
@RequestMapping("/api/walk")
@RequiredArgsConstructor
@Slf4j
public class WalkController {

  private final WalkService walkService;

  @GetMapping("/{walkId}")
  public ResponseEntity<WalkData> getWalk(@PathVariable Long walkId) {
    log.info("get Walk data for walkId: {}", walkId);
    return ResponseEntity.ok(walkService.getWalkById(walkId));
  }

  @PostMapping
  public ResponseEntity<Long> walk(@RequestBody WalkData data) {
    log.info("Walk save Request");
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(walkService.saveWalk(data));
  }
}
