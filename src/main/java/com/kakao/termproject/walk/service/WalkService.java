package com.kakao.termproject.walk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.walk.domain.Walk;
import com.kakao.termproject.walk.dto.WalkData;
import com.kakao.termproject.walk.repository.WalkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalkService {

  private final WalkRepository walkRepository;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public WalkData getWalkById(Long walkId) {
    Walk walk = walkRepository.findById(walkId)
      .orElseThrow(() -> new DataNotFoundException("해당되는 id의 산책 경로가 존재하지 않습니다."));

    return objectMapper.convertValue(walk.getWalk(), WalkData.class);
  }
}
