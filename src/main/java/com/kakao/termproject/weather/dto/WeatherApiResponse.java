package com.kakao.termproject.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record WeatherApiResponse(
    List<WeatherList> list
) {

  public record WeatherList(
      @JsonProperty("dt_txt") String dateTime,
      Main main,
      List<Weather> weather,
      @JsonProperty("pop") double precipitationProbability,
      Wind wind,
      Sys sys
  ) {

  }

  public record Main(
      double temp
  ) {

  }

  public record Weather(
      String main,
      String description
  ) {

  }

  public record Wind(
      double speed
  ) {

  }

  public record Sys(
      String pod //"d" 또는 "n". day or night
  ) {

  }
}
