package com.kakao.termproject.walkscore;

import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.walk.domain.Walk;
import com.kakao.termproject.walkscore.dto.WalkScoreContext;
import com.kakao.termproject.weather.dto.WeatherDetailInternal;
import com.kakao.termproject.walkscore.policy.EnvironmentalScorePolicy;
import com.kakao.termproject.walkscore.policy.PersonalizationWeightPolicy;
import com.kakao.termproject.walkscore.policy.RouteScorePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalkScoreCalculator {

  private final RouteScorePolicy routeScorePolicy;
  private final EnvironmentalScorePolicy environmentalScorePolicy;
  private final PersonalizationWeightPolicy personalizationWeightPolicy;

  public int calculateWalkScore(WeatherDetailInternal detail, Pet pet, Walk walk) {
    WalkScoreContext context = new WalkScoreContext(detail, pet, walk);

    int baseRouteScore = routeScorePolicy.calculateScore(context);
    int environmentalScore = environmentalScorePolicy.calculateScore(context);
    double personalizationWeight = personalizationWeightPolicy.calculateWeight(context);

    //최종 경로 점수(PRS) = (기본 경로 점수 + 환경 변수 점수) * 개인화 가중치
    double calculatedScore = (baseRouteScore + environmentalScore) * personalizationWeight;

    return (int) Math.round(calculatedScore);
  }

}
