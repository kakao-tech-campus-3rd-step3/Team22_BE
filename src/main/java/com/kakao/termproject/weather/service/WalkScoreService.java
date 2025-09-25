package com.kakao.termproject.weather.service;

import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.pet.service.PetService;
import com.kakao.termproject.weather.dto.WeatherDetailInternal;
import com.kakao.termproject.weather.dto.WeatherResponse;
import com.kakao.termproject.weather.dto.WeatherResponse.HourlyForecast;
import com.kakao.termproject.weather.dto.WeatherResponse.HourlyForecast.WeatherDetail;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalkScoreService {

  private final WalkScoreCalculator walkScoreCalculator;
  private final PetService petService;

  public WeatherResponse getWalkScoreForecast(List<WeatherDetailInternal> weatherDetailInternals,
      Long petId) {
    Pet pet = petService.get(petId);

    List<HourlyForecast> forecasts = weatherDetailInternals.stream()
        .map(forecast -> new HourlyForecast(
            new WeatherDetail(
                forecast.dateTime(),
                forecast.weather(),
                forecast.temperature(),
                forecast.humidity(),
                forecast.precipitationProbability(),
                forecast.windSpeed(),
                forecast.windDegree()
            ),
            walkScoreCalculator.calculateWalkScore(forecast, pet)
        )).collect(Collectors.toList());

    return new WeatherResponse(forecasts);
  }

}
