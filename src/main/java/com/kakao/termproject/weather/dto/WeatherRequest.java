package com.kakao.termproject.weather.dto;

public record WeatherRequest(
    double lat,
    double lon,
    int cnt
) {

}
