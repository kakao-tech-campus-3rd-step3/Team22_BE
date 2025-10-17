package com.kakao.termproject.walkscore.service;

import com.kakao.termproject.exception.custom.DataNotFoundException;
import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.walk.domain.Walk;
import com.kakao.termproject.walk.repository.WalkRepository;
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
  private final WalkRepository walkRepository;

  public WeatherResponse getWalkScoreForecast(List<WeatherDetailInternal> weatherDetailInternals,
      Member member) {
    Pet pet = member.getPet();
    if (pet == null) {
      throw new DataNotFoundException("해당하는 사용자의 반려견 정보가 존재하지 않습니다.");
    }
    Walk walk = walkRepository.findByMember(member)
        .orElseThrow(() -> new DataNotFoundException("해당하는 사용자의 주 산책 경로가 존재하지 않습니다."));

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
