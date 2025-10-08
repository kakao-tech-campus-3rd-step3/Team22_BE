package com.kakao.termproject.weather.service;

import com.kakao.termproject.weather.domain.WeatherCondition;
import com.kakao.termproject.weather.dto.AirPollutionApiResponse;
import com.kakao.termproject.weather.dto.AirPollutionInfo;
import com.kakao.termproject.weather.dto.WeatherApiResponse;
import com.kakao.termproject.weather.dto.WeatherDetailInternal;
import com.kakao.termproject.weather.dto.WeatherRequest;
import com.kakao.termproject.weather.properties.OpenWeatherMapProperties;
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
  private final OpenWeatherMapProperties openWeatherMapProperties;

  public List<WeatherDetailInternal> getInternalForecasts(WeatherRequest request) {
    WeatherApiResponse weatherApiResponse = fetchWeatherApi(request);
    AirPollutionApiResponse airApiResponse = fetchAirPollutionApi(request);

    Map<Long, AirPollutionInfo> pollutionInfoMap = createPollutionInfoMap(airApiResponse);
    return createInternalForecasts(weatherApiResponse, pollutionInfoMap);
  }

  private WeatherApiResponse fetchWeatherApi(WeatherRequest request) {
    final String units = "metric";

    URI uri = UriComponentsBuilder
        .fromHttpUrl(openWeatherMapProperties.baseUrl().weather())
        .queryParam("lat", request.lat())
        .queryParam("lon", request.lon())
        .queryParam("units", units)
        .queryParam("appid", openWeatherMapProperties.key())
        .queryParam("cnt", request.cnt())
        .build()
        .toUri();

    return restTemplate.getForObject(uri, WeatherApiResponse.class);
  }

  private AirPollutionApiResponse fetchAirPollutionApi(WeatherRequest request) {
    URI airUri = UriComponentsBuilder
        .fromHttpUrl(openWeatherMapProperties.baseUrl().airPollution())
        .queryParam("lat", request.lat())
        .queryParam("lon", request.lon())
        .queryParam("appid", openWeatherMapProperties.key())
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

  private LocalDateTime convertUtcToKst(String utcDateTimeStr) {
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime localDateTime = LocalDateTime.parse(utcDateTimeStr, FORMATTER);
    ZonedDateTime utcZoned = localDateTime.atZone(ZoneId.of("UTC"));
    ZonedDateTime kstZoned = utcZoned.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
    return kstZoned.toLocalDateTime();
  }

}
