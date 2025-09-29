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
    if (pm2_5Value >= VERY_BAD.threshold) {
      return VERY_BAD;
    }
    if (pm2_5Value >= BAD.threshold) {
      return BAD;
    }
    if (pm2_5Value >= MODERATE.threshold) {
      return MODERATE;
    }
    return GOOD;
  }

}
