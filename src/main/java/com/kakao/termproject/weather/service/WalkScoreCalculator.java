package com.kakao.termproject.weather.service;

import com.kakao.termproject.weather.domain.AirQualityGrade;
import com.kakao.termproject.weather.dto.WeatherDetailInternal;
import org.springframework.stereotype.Component;

@Component
public class WalkScoreCalculator {

  private static final double HIGH_TEMP_THRESHOLD = 25.0;
  private static final double LOW_TEMP_THRESHOLD = 4.0;
  private static final int TEMP_PENALTY_RATE = 5;

  private static final double HIGH_HUMIDITY_THRESHOLD = 60.0;
  private static final int HUMIDITY_PENALTY_DIVISOR = 5;
  private static final int HUMIDITY_PENALTY_RATE = 3;

  public int calculateWalkScore(WeatherDetailInternal detail) {
    int score = 50;

    //기온/습도
    double temperature = detail.temperature();
    if (temperature > HIGH_TEMP_THRESHOLD) {
      //25℃ 초과 시 1℃당 -5점
      score -= ((int) (temperature - HIGH_TEMP_THRESHOLD)) * TEMP_PENALTY_RATE;
    }
    if (temperature < LOW_TEMP_THRESHOLD) {
      //4℃ 미만일 경우 1℃당 -5점
      score -= ((int) (LOW_TEMP_THRESHOLD - temperature)) * TEMP_PENALTY_RATE;
    }

    double humidity = detail.humidity();
    if (humidity > HIGH_HUMIDITY_THRESHOLD) {
      //60% 초과 시 5%당 -3점
      int penaltySteps = (int) ((humidity - HIGH_HUMIDITY_THRESHOLD) / HUMIDITY_PENALTY_DIVISOR);
      score -= penaltySteps * HUMIDITY_PENALTY_RATE;
    }

    //대기질
    double pm2_5 = detail.pm2_5();
    AirQualityGrade grade = AirQualityGrade.of(pm2_5);
    score -= grade.getPenalty();

    return score;
  }

}
