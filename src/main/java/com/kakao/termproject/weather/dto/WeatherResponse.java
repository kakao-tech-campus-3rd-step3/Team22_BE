package com.kakao.termproject.weather.dto;

import com.kakao.termproject.weather.WeatherCondition;
import java.util.List;

public record WeatherResponse(List<HourlyForecast> items) {

  public record HourlyForecast(
      WeatherDetail weatherDetail,
      int walkScore
  ) {

    public record WeatherDetail(
        String time,
        WeatherCondition condition,
        double temperature,
        int humidity,
        double precipitationProbability, //강수확률 (0~1)
        double windSpeed,
        int windDegree //풍향 (도, 0~360, 북쪽 기준 시계 방향)
    ) {

    }
  }
}
