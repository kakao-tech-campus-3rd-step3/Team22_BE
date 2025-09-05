package com.kakao.termproject.weather;


import com.kakao.termproject.weather.dto.WeatherResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

  private final WeatherService weatherService;

  public WeatherController(WeatherService weatherService) {
    this.weatherService = weatherService;
  }

  //날씨 상세 정보
  @GetMapping
  public ResponseEntity<WeatherResponse> getWeatherDetail(
      @RequestParam double lat, @RequestParam double lon, @RequestParam int cnt
  ) {
    WeatherResponse response = weatherService.getWeatherDetail(lat, lon, cnt);

    return ResponseEntity.ok(response);
  }

}
