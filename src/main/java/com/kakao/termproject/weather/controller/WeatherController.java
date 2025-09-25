package com.kakao.termproject.weather.controller;


import com.kakao.termproject.weather.dto.WeatherDetailInternal;
import com.kakao.termproject.weather.dto.WeatherRequest;
import com.kakao.termproject.weather.dto.WeatherResponse;
import com.kakao.termproject.weather.service.WalkScoreService;
import com.kakao.termproject.weather.service.WeatherService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

  private final WeatherService weatherService;
  private final WalkScoreService walkScoreService;

  //날씨 상세 정보
  @GetMapping
  public ResponseEntity<WeatherResponse> getWeatherDetail(WeatherRequest request) {
    List<WeatherDetailInternal> internalForecasts = weatherService.getInternalForecasts(request);
    WeatherResponse response = walkScoreService.getWalkScoreForecast(internalForecasts, request.petId());
    return ResponseEntity.ok(response);
  }

}
