package com.kakao.termproject.map.controller;

import com.kakao.termproject.map.dto.MapRequest;
import com.kakao.termproject.map.dto.MapResponse;
import com.kakao.termproject.map.service.MapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map")
@Slf4j
public class MapController {

  private final MapService mapService;

  @PostMapping
  public ResponseEntity<MapResponse> getFitness(@RequestBody MapRequest request) {
    log.info("get fitness request");
    return ResponseEntity.ok(mapService.getFitness(request));
  }
}
