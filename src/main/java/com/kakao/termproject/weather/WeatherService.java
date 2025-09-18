package com.kakao.termproject.weather;

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

  @Value("${openweathermap.api.key}")
  private String apiKey;
  private final RestTemplate restTemplate;

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
        .fromHttpUrl("https://pro.openweathermap.org/data/2.5/forecast/hourly")
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
        .fromHttpUrl("http://api.openweathermap.org/data/2.5/air_pollution/forecast")
        .queryParam("lat", request.lat())
        .queryParam("lon", request.lon())
        .queryParam("appid", apiKey)
        .build()
        .toUri();

    return restTemplate.getForObject(airUri, AirPollutionApiResponse.class);
  }

  private Map<Long, AirPollutionInfo> createPollutionInfoMap(
      AirPollutionApiResponse airApiResponse) {
    return airApiResponse.list().stream()
        .collect(Collectors.toMap(
            AirPollutionApiResponse.AirPollutionForecastItem::dt,
            item -> new AirPollutionInfo(
                item.main().aqi(),
                item.components().getOrDefault("pm2_5", 0.0)
            )
        ));
  }

  private List<WeatherDetailInternal> createInternalForecasts(
      WeatherApiResponse weatherApiResponse, Map<Long, AirPollutionInfo> pollutionInfoMap) {
    return weatherApiResponse.list().stream()
        .map(apiItem -> new WeatherDetailInternal(
            convertUtcToKst(apiItem.dateTime()),
            apiItem.main().temp(),
            apiItem.main().humidity(),
            WeatherCondition.from(apiItem.weather().get(0).main(), apiItem.sys().pod()),
            apiItem.precipitationProbability(),
            apiItem.wind().speed(),
            apiItem.wind().deg(),
            pollutionInfoMap.get(apiItem.dt()).aqi(),
            pollutionInfoMap.get(apiItem.dt()).pm2_5()
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
            calculateWalkScore(item)
        )).collect(Collectors.toList());
  }

  private int calculateWalkScore(WeatherDetailInternal detail) {
    int score = 0;

    //기온/습도
    double temperature = detail.temperature();
    if (temperature > 25) {
      score -= (int) ((temperature - 25) * 5); // 25℃ 초과 시 1℃당 -5점
    }

    double humidity = detail.humidity();
    if (humidity > 60) {
      score -= (int) (((humidity - 60) / 5) * 3); // 60% 초과 시 5%당 -3점
    }

    //대기질
    double pm2_5 = detail.pm2_5();
    if (pm2_5 >= 76) {
      score -= 40;
    } else if (pm2_5 >= 36) {
      score -= 20;
    } else if (pm2_5 >= 16) {
      score += 0;
    } else if (pm2_5 >= 0) {
      score += 10;
    }

    return score;
  }

  private LocalDateTime convertUtcToKst(String utcDateTimeStr) {
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime localDateTime = LocalDateTime.parse(utcDateTimeStr, FORMATTER);
    ZonedDateTime utcZoned = localDateTime.atZone(ZoneId.of("UTC"));
    ZonedDateTime kstZoned = utcZoned.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
    return kstZoned.toLocalDateTime();
  }

}
