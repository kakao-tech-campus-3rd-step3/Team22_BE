package com.kakao.termproject.walk.service;

import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.map.dto.MapResponse;
import com.kakao.termproject.map.service.MapService;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.walk.domain.Walk;
import com.kakao.termproject.walk.dto.WalkData;
import com.kakao.termproject.walk.dto.WalkResponse;
import com.kakao.termproject.walk.dto.WalkSaveResponse;
import com.kakao.termproject.walk.repository.WalkRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalkService {

  private final WalkRepository walkRepository;
  private final MapService mapService;

  @Transactional(readOnly = true)
  public WalkResponse getWalkById(Long walkId) {
    Walk walk = walkRepository.findById(walkId)
        .orElseThrow(() -> new DataNotFoundException("해당되는 id의 산책 경로가 존재하지 않습니다."));

    updateSlopes(walk);

    return new WalkResponse(
        walk.getId(),
        walk.getMaxSlope(),
        walk.getAvgOfSlope(),
        walk.getWalk()
    );
  }

  private void updateSlopes(Walk walk) {
    MapResponse mapResponse = mapService.getFitness(walk.getWalk().coordinates());

    walk.updateSlopes(
        mapResponse.maxSlope(),
        mapResponse.avgOfSlope(),
        LocalDateTime.now()
    );
  }

  @Transactional
  public WalkSaveResponse saveWalk(Member member, WalkData walkData) {
    MapResponse mapResponse = mapService.getFitness(walkData.coordinates());

    return walkRepository.findByMember(member)
      .map(walk -> new WalkSaveResponse(HttpStatus.OK, updateWalk(walk, walkData, mapResponse)))
      .orElseGet(
        () -> new WalkSaveResponse(HttpStatus.CREATED, createWalk(walkData, mapResponse, member)));
  }

  private WalkResponse updateWalk(Walk walk, WalkData walkData, MapResponse mapResponse) {
    walk.updateWalk(
      walkData,
      mapResponse.maxSlope(),
      mapResponse.avgOfSlope(),
      LocalDateTime.now()
    );

    return convertToDTO(walk);
  }

  private WalkResponse createWalk(WalkData walkData, MapResponse mapResponse, Member member) {
    Walk walk = walkRepository.save(
        new Walk(
            walkData,
            mapResponse.maxSlope(),
            mapResponse.avgOfSlope(),
            LocalDateTime.now()
        )
    );

    return convertToDTO(walk);
  }

  private WalkResponse convertToDTO(Walk walk) {
    return new WalkResponse(
      walk.getId(),
      walk.getMaxSlope(),
      walk.getAvgOfSlope(),
      walk.getUpdateDateTime(),
      walk.getWalk()
    );
  }

  public Walk get(Long walkId) {
    return walkRepository.findById(walkId)
        .orElseThrow(() -> new DataNotFoundException("해당되는 id의 산책 경로가 존재하지 않습니다."));
  }
}
