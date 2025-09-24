package com.kakao.termproject.weather.service;

import com.kakao.termproject.weather.domain.WeatherCondition;
import com.kakao.termproject.weather.dto.AirPollutionApiResponse;
import com.kakao.termproject.weather.dto.AirPollutionInfo;
import com.kakao.termproject.weather.dto.WeatherApiResponse;
import com.kakao.termproject.weather.dto.WeatherDetailInternal;
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
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class WeatherService {

  private final RestTemplate restTemplate;
  private final WalkScoreCalculator walkScoreCalculator;

  @Value("${openweathermap.api.base-url.weather}")
  private String weatherApiBaseUrl;

  @Value("${openweathermap.api.base-url.air-pollution}")
  private String airPollutionApiBaseUrl;

  @Value("${openweathermap.api.key}")
  private String apiKey;

  public WeatherResponse getWeatherDetail(WeatherRequest request) {
    WeatherApiResponse weatherApiResponse = fetchWeatherApi(request);
    AirPollutionApiResponse airApiResponse = fetchAirPollutionApi(request);

    Map<Long, AirPollutionInfo> pollutionInfoMap = createPollutionInfoMap(airApiResponse);
    List<WeatherDetailInternal> internalForecasts = createInternalForecasts(weatherApiResponse,
        pollutionInfoMap);
    List<HourlyForecast> items = createHourlyForecasts(internalForecasts);

    return new WeatherResponse(items);
  }

  private WeatherApiResponse fetchWeatherApi(WeatherRequest request) {
    final String units = "metric";

    URI uri = UriComponentsBuilder
        .fromHttpUrl(weatherApiBaseUrl)
        .queryParam("lat", request.lat())
        .queryParam("lon", request.lon())
        .queryParam("units", units)
        .queryParam("appid", apiKey)
        .queryParam("cnt", request.cnt())
        .build()
        .toUri();

    return restTemplate.getForObject(uri, WeatherApiResponse.class);
  }

  private AirPollutionApiResponse fetchAirPollutionApi(WeatherRequest request) {
    URI airUri = UriComponentsBuilder
        .fromHttpUrl(airPollutionApiBaseUrl)
        .queryParam("lat", request.lat())
        .queryParam("lon", request.lon())
        .queryParam("appid", apiKey)
        .build()
        .toUri();

    return restTemplate.getForObject(airUri, AirPollutionApiResponse.class);
  }

  private Map<Long, AirPollutionInfo> createPollutionInfoMap(
      AirPollutionApiResponse airApiResponse) {
    return airApiResponse.airPollutionForecastItems().stream()
        .collect(Collectors.toMap(
            AirPollutionApiResponse.AirPollutionForecastItem::timestamp,
            item -> new AirPollutionInfo(
                item.airQualityInfo().aqi(),
                item.components().getOrDefault("pm2_5", 0.0)
            )
        ));
  }

  private List<WeatherDetailInternal> createInternalForecasts(
      WeatherApiResponse weatherApiResponse, Map<Long, AirPollutionInfo> pollutionInfoMap) {
    return weatherApiResponse.weatherApiForecastItems().stream()
        .map(apiItem -> new WeatherDetailInternal(
            convertUtcToKst(apiItem.dateTime()),
            apiItem.weatherMetrics().temperature(),
            apiItem.weatherMetrics().humidity(),
            WeatherCondition.from(apiItem.weather().get(0).weatherCondition(),
                apiItem.sysInfo().partOfDay()),
            apiItem.precipitationProbability(),
            apiItem.wind().speed(),
            apiItem.wind().degree(),
            pollutionInfoMap.get(apiItem.timestamp()).aqi(),
            pollutionInfoMap.get(apiItem.timestamp()).pm2_5()
        )).collect(Collectors.toList());
  }

  private List<HourlyForecast> createHourlyForecasts(
      List<WeatherDetailInternal> internalForecasts) {
    return internalForecasts.stream()
        .map(item -> new HourlyForecast(
            new WeatherDetail(
                item.dateTime(),
                item.weather(),
                item.temperature(),
                item.humidity(),
                item.precipitationProbability(),
                item.windSpeed(),
                item.windDegree()
            ),
            walkScoreCalculator.calculateWalkScore(item)
        )).collect(Collectors.toList());
  }

  private LocalDateTime convertUtcToKst(String utcDateTimeStr) {
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime localDateTime = LocalDateTime.parse(utcDateTimeStr, FORMATTER);
    ZonedDateTime utcZoned = localDateTime.atZone(ZoneId.of("UTC"));
    ZonedDateTime kstZoned = utcZoned.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
    return kstZoned.toLocalDateTime();
  }

}
