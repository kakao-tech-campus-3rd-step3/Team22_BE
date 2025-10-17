package com.kakao.termproject.weather.domain;

import java.util.Arrays;
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
    return Arrays.stream(values())
        .filter(grade -> pm2_5Value >= grade.threshold)
        .findFirst()
        .orElse(GOOD);
  }

}
