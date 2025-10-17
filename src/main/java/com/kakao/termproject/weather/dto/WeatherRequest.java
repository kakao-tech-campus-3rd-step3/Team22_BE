package com.kakao.termproject.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record WeatherRequest(
    @Schema(example = "37.5665")
    double lat,

    @Schema(example = "126.9780")
    double lon,

    @Schema(example = "3")
    int cnt
) {

}
