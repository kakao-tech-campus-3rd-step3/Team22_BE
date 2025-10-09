package com.kakao.termproject.walkscore.policy;

import com.kakao.termproject.walkscore.dto.WalkScoreContext;
import org.springframework.stereotype.Component;

@Component
public class RouteScorePolicy implements ScorePolicy {

  // 경로 점수 계산에 필요한 상수들
  private final double SLOPE_THRESHOLD = 5.0; // 점수 차감이 시작되는 경사도 기준 (5%)
  private final int SLOPE_PENALTY_RATE = 2; // 경사도가 1% 증가할 때마다 차감될 점수

  @Override
  public int calculateScore(WalkScoreContext context) {
    int score = 20; //기본 점수
    double absAvgSlope = Math.abs(context.walk().getAvgOfSlope());

    //평균 경사도 5% 미만: 20점,이후 1% 증가 시 -2점
    if (absAvgSlope > SLOPE_THRESHOLD) {
      score -= ((int) (absAvgSlope - SLOPE_THRESHOLD)) * SLOPE_PENALTY_RATE;
    }
    return score;
  }

}
