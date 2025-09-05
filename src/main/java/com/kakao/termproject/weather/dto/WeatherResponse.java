package com.kakao.termproject.weather.dto;

import com.kakao.termproject.weather.WeatherCondition;
import java.util.List;

public record WeatherResponse(List<WeatherItem> items) {

  public record WeatherItem(
      String time,
      WeatherCondition condition,
      double temperature,
      double precipitationProbability, //강수확률 (0~1)
      double windSpeed
  ) {

  }
}
