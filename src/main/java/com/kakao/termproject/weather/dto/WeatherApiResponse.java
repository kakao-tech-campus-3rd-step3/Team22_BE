package com.kakao.termproject.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record WeatherApiResponse(
    List<WeatherApiForecastItem> list
) {

  public record WeatherApiForecastItem(
      @JsonProperty("dt") Long dt,
      @JsonProperty("dt_txt") String dateTime,
      Main main,
      List<Weather> weather,
      @JsonProperty("pop") double precipitationProbability,
      Wind wind,
      Sys sys
  ) {

  }

  public record Main(
      double temp,
      int humidity
  ) {

  }

  public record Weather(
      String main
  ) {

  }

  public record Wind(
      double speed,
      int deg
  ) {

  }

  public record Sys(
      String pod //"d" 또는 "n". day or night
  ) {

  }
}
