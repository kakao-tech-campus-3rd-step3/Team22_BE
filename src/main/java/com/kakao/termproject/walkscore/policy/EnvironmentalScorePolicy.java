package com.kakao.termproject.walkscore.policy;

import com.kakao.termproject.weather.domain.AirQualityGrade;
import com.kakao.termproject.walkscore.dto.WalkScoreContext;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentalScorePolicy implements ScorePolicy {

  // 환경 점수 계산에 필요한 상수들
  private static final double HIGH_TEMP_THRESHOLD = 25.0;
  private static final double LOW_TEMP_THRESHOLD = 4.0;
  private static final int TEMP_PENALTY_RATE = 5;

  private static final int HIGH_HUMIDITY_THRESHOLD = 60;
  private static final int HUMIDITY_PENALTY_DIVISOR = 5;
  private static final int HUMIDITY_PENALTY_RATE = 3;

  @Override
  public int calculateScore(WalkScoreContext context) {
    int score = 50; //기본 점수

    //기온/습도
    double temperature = context.weatherDetail().temperature();
    if (temperature > HIGH_TEMP_THRESHOLD) {
      //25℃ 초과 시 1℃당 -5점
      score -= ((int) (temperature - HIGH_TEMP_THRESHOLD)) * TEMP_PENALTY_RATE;
    }
    if (temperature < LOW_TEMP_THRESHOLD) {
      //4℃ 미만일 경우 1℃당 -5점
      score -= ((int) (LOW_TEMP_THRESHOLD - temperature)) * TEMP_PENALTY_RATE;
    }

    double humidity = context.weatherDetail().humidity();
    if (humidity > HIGH_HUMIDITY_THRESHOLD) {
      //60% 초과 시 5%당 -3점
      int penaltySteps = (int) ((humidity - HIGH_HUMIDITY_THRESHOLD) / HUMIDITY_PENALTY_DIVISOR);
      score -= penaltySteps * HUMIDITY_PENALTY_RATE;
    }

    //대기질
    double pm2_5 = context.weatherDetail().pm2_5();
    AirQualityGrade grade = AirQualityGrade.of(pm2_5);
    score -= grade.getPenalty();

    return score;
  }

}
