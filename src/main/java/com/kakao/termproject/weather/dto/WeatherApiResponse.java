package com.kakao.termproject.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record WeatherApiResponse(
    @JsonProperty("list")
    List<WeatherApiForecastItem> weatherApiForecastItems
) {

  public record WeatherApiForecastItem(
      @JsonProperty("dt") Long timestamp,
      @JsonProperty("dt_txt") String dateTime,
      @JsonProperty("main") WeatherMetrics weatherMetrics,
      List<Weather> weather,
      @JsonProperty("pop") double precipitationProbability, //강수확률
      Wind wind,
      @JsonProperty("sys") SysInfo sysInfo
  ) {

  }

  public record WeatherMetrics(
      @JsonProperty("temp") double temperature,
      int humidity
  ) {

  }

  public record Weather(
      @JsonProperty("main") String weatherCondition
  ) {

  }

  public record Wind(
      double speed,
      @JsonProperty("deg") int degree
  ) {

  }

  public record SysInfo(
      String partOfDay //"d" 또는 "n". day or night
  ) {

  }
}
