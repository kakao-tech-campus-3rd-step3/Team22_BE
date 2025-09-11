package com.kakao.termproject.weather;

import com.kakao.termproject.weather.dto.WeatherApiResponse;
import com.kakao.termproject.weather.dto.WeatherApiResponse.WeatherApiForecastItem;
import com.kakao.termproject.weather.dto.WeatherRequest;
import com.kakao.termproject.weather.dto.WeatherResponse;
import com.kakao.termproject.weather.dto.WeatherResponse.HourlyForecast;
import com.kakao.termproject.weather.dto.WeatherResponse.HourlyForecast.WeatherDetail;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

  private final String units = "metric"; //섭씨

  public WeatherResponse getWeatherDetail(WeatherRequest request) {
    URI uri = UriComponentsBuilder
        .fromHttpUrl("https://pro.openweathermap.org/data/2.5/forecast/hourly")
        .queryParam("lat", request.lat())
        .queryParam("lon", request.lon())
        .queryParam("units", units)
        .queryParam("appid", apiKey)
        .queryParam("cnt", request.cnt())
        .build()
        .toUri();

    WeatherApiResponse apiResponse = restTemplate.getForObject(uri, WeatherApiResponse.class);

    List<HourlyForecast> items = apiResponse.list().stream()
        .map(this::convertToHourlyForecast)
        .collect(Collectors.toList());

    return new WeatherResponse(items);
  }

  private int calculateWalkScore(WeatherDetail detail) {

    return 1;
  }

  private HourlyForecast convertToHourlyForecast(WeatherApiForecastItem item) {
    WeatherDetail detail = new WeatherDetail(
        convertUtcToKst(item.dateTime()),
        WeatherCondition.from(item.weather().get(0).main(), item.sys().pod()),
        item.main().temp(),
        item.main().humidity(),
        item.precipitationProbability(),
        item.wind().speed(),
        item.wind().deg());

    return new HourlyForecast(detail, calculateWalkScore(detail));
  }

  private String convertUtcToKst(String utcDateTimeStr) {
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime localDateTime = LocalDateTime.parse(utcDateTimeStr, FORMATTER);
    ZonedDateTime utcZoned = localDateTime.atZone(ZoneId.of("UTC"));
    ZonedDateTime kstZoned = utcZoned.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
    return kstZoned.format(FORMATTER);
  }

}
