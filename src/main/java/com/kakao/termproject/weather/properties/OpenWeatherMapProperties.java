package com.kakao.termproject.weather.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openweathermap.api")
public record OpenWeatherMapProperties(
    BaseUrl baseUrl,
    String key
) {

  public record BaseUrl(
      String weather,
      String airPollution
  ) {

  }
}
