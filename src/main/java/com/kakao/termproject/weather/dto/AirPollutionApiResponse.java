package com.kakao.termproject.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record AirPollutionApiResponse(
    List<AirPollutionForecastItem> list
) {

  public record AirPollutionForecastItem(
      @JsonProperty("dt") long dt, // timestamp
      Map<String, Double> components,
      Main main
  ) {

  }

  public record Main(
      int aqi //(Air Quality Index) 전반적인 대기 질의 상태. 1~5(1은 매우 좋음, 5는 매우 나쁨)
  ) {

  }
}
