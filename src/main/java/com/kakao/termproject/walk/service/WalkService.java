package com.kakao.termproject.walk.service;

import com.kakao.termproject.exception.custom.DataAlreadyExistException;
import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.map.dto.MapResponse;
import com.kakao.termproject.map.service.MapService;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.walk.domain.Walk;
import com.kakao.termproject.walk.dto.WalkData;
import com.kakao.termproject.walk.dto.WalkResponse;
import com.kakao.termproject.walk.repository.WalkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalkService {

  private final WalkRepository walkRepository;
  private final MapService mapService;

  @Transactional
  public WalkResponse getWalk(Member member) {
    Walk walk = walkRepository.findByMember(member)
      .orElseThrow(() -> new DataNotFoundException("해당하는 사용자의 주 산책 경로가 존재하지 않습니다."));

    updateSlopes(walk);

    return convertToDTO(walk);
  }

  private void updateSlopes(Walk walk) {
    MapResponse mapResponse = mapService.getFitness(walk.getWalk().coordinates());

    walk.updateSlopes(
      mapResponse.maxSlope(),
      mapResponse.avgOfSlope()
    );
  }

  @Transactional
  public WalkResponse saveWalk(Member member, WalkData walkData) {

    walkRepository.findByMember(member)
      .ifPresent(w -> {
        throw new DataAlreadyExistException("해당하는 사용자의 주 산책 경로가 이미 존재합니다.");
      });
    
    MapResponse mapResponse = mapService.getFitness(walkData.coordinates());

    Walk createdWalk = walkRepository.save(
      new Walk(
        walkData,
        mapResponse.maxSlope(),
        mapResponse.avgOfSlope(),
        member
      )
    );

    return convertToDTO(createdWalk);
  }

  @Transactional
  public WalkResponse updateWalk(Member member, WalkData walkData) {
    Walk walk = walkRepository.findByMember(member)
      .orElseThrow(() -> new DataNotFoundException("해당하는 사용자의 주 산책 경로가 존재하지 않습니다."));

    MapResponse mapResponse = mapService.getFitness(walkData.coordinates());

    walk.updateWalk(
      walkData,
      mapResponse.maxSlope(),
      mapResponse.avgOfSlope()
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
