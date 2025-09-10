package com.kakao.termproject.weather;

import com.kakao.termproject.weather.dto.WeatherApiResponse;
import com.kakao.termproject.weather.dto.WeatherRequest;
import com.kakao.termproject.weather.dto.WeatherResponse;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class WeatherService {

  @Value("${openweathermap.api.key}")
  private String apiKey;

  private final RestTemplate restTemplate;

  public WeatherResponse getWeatherDetail(WeatherRequest request) {
    URI uri = UriComponentsBuilder
        .fromHttpUrl("https://pro.openweathermap.org/data/2.5/forecast/hourly")
        .queryParam("lat", request.lat())
        .queryParam("lon", request.lon())
        .queryParam("units", "metric")
        .queryParam("appid", apiKey)
        .queryParam("cnt", request.cnt())
        .build()
        .toUri();

    WeatherApiResponse apiResponse = restTemplate.getForObject(uri, WeatherApiResponse.class);

    List<WeatherResponse.WeatherItem> items = apiResponse.list().stream()
        .map(item -> new WeatherResponse.WeatherItem(
            item.dateTime(),
            WeatherCondition.from(item.weather().get(0).main(), item.sys().pod()),
            item.main().temp(),
            item.precipitationProbability(),
            item.wind().speed()
        ))
        .collect(Collectors.toList());

    return new WeatherResponse(items);
  }
}
