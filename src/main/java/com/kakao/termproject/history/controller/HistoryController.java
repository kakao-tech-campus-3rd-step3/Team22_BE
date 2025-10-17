package com.kakao.termproject.history.controller;

import com.kakao.termproject.history.dto.HistoryResponse;
import com.kakao.termproject.history.service.HistoryService;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.walk.dto.WalkData;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/histories")
@RequiredArgsConstructor
@Slf4j
public class HistoryController {

  private final HistoryService historyService;

  @PostMapping
  public ResponseEntity<HistoryResponse> saveHistory(
    @AuthenticationPrincipal Member member,
    @RequestBody WalkData data
  ) {
    log.info("History save Request");
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(historyService.saveHistory(data, member));
  }

  @GetMapping("/{id}")
  public ResponseEntity<HistoryResponse> getHistory(
    @AuthenticationPrincipal Member member,
    @PathVariable Long id
  ) {
    log.info("History get request");
    return ResponseEntity.ok(historyService.getHistory(id, member));
  }

  @GetMapping
  public ResponseEntity<List<HistoryResponse>> getHistoryList(
    @AuthenticationPrincipal Member member
  ) {
    log.info("History get list request");
    return ResponseEntity.ok(historyService.getAllHistories(member));
  }
}
