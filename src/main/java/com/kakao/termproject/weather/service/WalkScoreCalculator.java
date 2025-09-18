package com.kakao.termproject.weather.service;

import com.kakao.termproject.weather.dto.WeatherDetailInternal;
import org.springframework.stereotype.Component;

@Component
public class WalkScoreCalculator {

  public int calculateWalkScore(WeatherDetailInternal detail) {
    int score = 50;

    //기온/습도
    double temperature = detail.temperature();
    if (temperature > 25) {
      score -= ((int) (temperature - 25)) * 5; // 25℃ 초과 시 1℃당 -5점
    }

    double humidity = detail.humidity();
    if (humidity > 60) {
      score -= ((int) ((humidity - 60) / 5)) * 3; // 60% 초과 시 5%당 -3점
    }

    //대기질
    double pm2_5 = detail.pm2_5();
    if (pm2_5 >= 76) {
      score -= 40;
    } else if (pm2_5 >= 36) {
      score -= 20;
    } else if (pm2_5 >= 16) {
      score += 0;
    } else if (pm2_5 >= 0) {
      score += 10;
    }

    return score;
  }

}
