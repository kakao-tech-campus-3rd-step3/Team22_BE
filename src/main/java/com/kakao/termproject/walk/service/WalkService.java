package com.kakao.termproject.walk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.exception.custom.JsonParseException;
import com.kakao.termproject.map.dto.MapResponse;
import com.kakao.termproject.map.service.MapService;
import com.kakao.termproject.walk.domain.Walk;
import com.kakao.termproject.walk.dto.WalkData;
import com.kakao.termproject.walk.dto.WalkResponse;
import com.kakao.termproject.walk.repository.WalkRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalkService {

  private final WalkRepository walkRepository;
  private final MapService mapService;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Transactional(readOnly = true)
  public WalkResponse getWalkById(Long walkId) {
    try {
      Walk walk = walkRepository.findById(walkId)
        .orElseThrow(() -> new DataNotFoundException("해당되는 id의 산책 경로가 존재하지 않습니다."));

      WalkData walkData = objectMapper.readValue(walk.getWalk(), WalkData.class);

      long monthDiff = ChronoUnit.WEEKS.between(walk.getUpdateDateTime(), LocalDateTime.now());

      if (Math.abs(monthDiff) >= 2) { // 2주 간격으로 경사도 업데이트
        updateSlopes(walk, walkData);
      }

      return new WalkResponse(
        walk.getId(),
        walk.getMaxSlope(),
        walk.getAvgOfSlope(),
        walkData
      );
    } catch (JsonProcessingException e) {
      throw new JsonParseException("산책 경로 데이터 직렬화에 실패하였습니다.");
    }
  }

  @Transactional
  public void updateSlopes(Walk walk, WalkData walkData) {
    MapResponse mapResponse = mapService.getFitness(walkData.coordinates());

    walk.updateSlopes(
      mapResponse.maxSlope(),
      mapResponse.avgOfSlope(),
      LocalDateTime.now()
    );

    walkRepository.save(walk);
  }

  @Transactional
  public Long saveWalk(WalkData walkData) {
    try {
      String data = objectMapper.writeValueAsString(walkData);

      MapResponse mapResponse = mapService.getFitness(walkData.coordinates());

      Walk walk = walkRepository.save(
        new Walk(
          data,
          mapResponse.maxSlope(),
          mapResponse.avgOfSlope(),
          LocalDateTime.now()
        )
      );

      return walk.getId();
    } catch (JsonProcessingException e) {
      throw new JsonParseException("산책 경로 데이터 역직렬화에 실패하였습니다.");
    }
  }
}
