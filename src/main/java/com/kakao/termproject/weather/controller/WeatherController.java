package com.kakao.termproject.weather.controller;


import com.kakao.termproject.weather.dto.WeatherRequest;
import com.kakao.termproject.weather.dto.WeatherResponse;
import com.kakao.termproject.weather.service.WeatherService;
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

  //날씨 상세 정보
  @GetMapping
  public ResponseEntity<WeatherResponse> getWeatherDetail(WeatherRequest request) {
    WeatherResponse response = weatherService.getWeatherDetail(request);
    return ResponseEntity.ok(response);
  }

}
