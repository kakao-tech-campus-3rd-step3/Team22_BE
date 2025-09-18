package com.kakao.termproject.weather.dto;

import com.kakao.termproject.weather.domain.WeatherCondition;
import java.time.LocalDateTime;

public record WeatherDetailInternal(
    LocalDateTime dateTime,
    double temperature,
    int humidity,
    WeatherCondition weather,
    double precipitationProbability,
    double windSpeed,
    int windDegree,
    int aqi,
    double pm2_5
) {

}
