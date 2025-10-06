package com.kakao.termproject.weather.controller;


import com.kakao.termproject.weather.dto.WeatherDetailInternal;
import com.kakao.termproject.weather.dto.WeatherRequest;
import com.kakao.termproject.weather.dto.WeatherResponse;
import com.kakao.termproject.weather.service.WalkScoreService;
import com.kakao.termproject.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

  private final WeatherService weatherService;
  private final WalkScoreService walkScoreService;

  //날씨 상세 정보
  @Operation(summary = "시간별 산책 점수 예보 조회", description = "특정 반려견의 정보를 바탕으로 개인화된 시간별 산책 점수 예보를 조회합니다.  ex) /api/weather?lat=37.5665&lon=126.9780&cnt=2&petId=0&walkId=0")
  @GetMapping()
  public ResponseEntity<WeatherResponse> getWeatherDetail(WeatherRequest request,
      @RequestParam("petId") Long petId, @RequestParam("walkId") Long walkId) {
    List<WeatherDetailInternal> internalForecasts = weatherService.getInternalForecasts(request);
    WeatherResponse response = walkScoreService.getWalkScoreForecast(internalForecasts, petId, walkId);
    return ResponseEntity.ok(response);
  }

}
