package com.kakao.termproject.weather.dto;

import com.kakao.termproject.weather.domain.WeatherCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public record WeatherResponse(List<HourlyForecast> hourlyForecasts) {

  public record HourlyForecast(
      WeatherDetail weatherDetail,

      @Schema(description = "산책 점수", example = "40")
      int walkScore
  ) {

    public record WeatherDetail(
        @Schema(description = "예보 시간 (KST)", example = "2025-10-09T04:00:00")
        LocalDateTime time,

        @Schema(description = "날씨 상태", example = "CLEAR_DAY")
        WeatherCondition condition,

        @Schema(description = "온도 (°C)", example = "20.93")
        double temperature,

        @Schema(description = "습도 (%)", example = "72")
        int humidity,

        @Schema(
            description = "강수 확률", example = "0.7",
            minimum = "0.0", maximum = "1.0"
        )
        double precipitationProbability,

        @Schema(description = "풍속 (m/s)", example = "2.21")
        double windSpeed,

        @Schema(
            description = "풍향 (북쪽이 0°, 시계 방향으로)", example = "64",
            minimum = "0", maximum = "360"
        )
        int windDegree
    ) {

    }
  }
}