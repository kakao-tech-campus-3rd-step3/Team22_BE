package com.kakao.termproject.weather.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AirQualityGrade {
  VERY_BAD(76, 40),
  BAD(36, 20),
  MODERATE(16, 0),
  GOOD(0, -10);

  private final int threshold;
  private final int penalty;

  public static AirQualityGrade of(double pm2_5Value) {
    for (AirQualityGrade grade : AirQualityGrade.values()) {
      if (pm2_5Value >= grade.threshold) {
        return grade;
      }
    }
    return GOOD;
  }

}
