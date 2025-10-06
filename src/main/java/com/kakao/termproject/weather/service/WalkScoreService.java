package com.kakao.termproject.weather.service;

import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.pet.service.PetService;
import com.kakao.termproject.walk.domain.Walk;
import com.kakao.termproject.walk.service.WalkService;
import com.kakao.termproject.walkscore.WalkScoreCalculator;
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
  private final WalkService walkService;

  public WeatherResponse getWalkScoreForecast(List<WeatherDetailInternal> weatherDetailInternals,
      Long petId, Long walkId) {
    Pet pet = petService.get(petId);
    Walk walk = walkService.get(walkId);

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
            walkScoreCalculator.calculateWalkScore(forecast, pet, walk)
        )).collect(Collectors.toList());

    return new WeatherResponse(forecasts);
  }

}
