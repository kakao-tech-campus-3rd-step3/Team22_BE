package com.kakao.termproject.walk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.exception.custom.JsonParseException;
import com.kakao.termproject.walk.domain.Walk;
import com.kakao.termproject.walk.dto.WalkData;
import com.kakao.termproject.walk.repository.WalkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalkService {

  private final WalkRepository walkRepository;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Transactional(readOnly = true)
  public WalkData getWalkById(Long walkId) {
    try {
      Walk walk = walkRepository.findById(walkId)
        .orElseThrow(() -> new DataNotFoundException("해당되는 id의 산책 경로가 존재하지 않습니다."));

      return objectMapper.readValue(walk.getWalk(), WalkData.class);
    } catch (JsonProcessingException e) {
      throw new JsonParseException("산책 경로 데이터 직렬화에 실패하였습니다.");
    }
  }

  @Transactional
  public Long saveWalk(WalkData walkData) {
    try {
      String data = objectMapper.writeValueAsString(walkData);

      Walk walk = walkRepository.save(new Walk(data));

      return walk.getId();
    } catch (JsonProcessingException e) {
      throw new JsonParseException("산책 경로 데이터 역직렬화에 실패하였습니다.");
    }
  }
}
