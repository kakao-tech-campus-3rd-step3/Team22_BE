package com.kakao.termproject.weather.dto;

public record AirPollutionInfo(
    int aqi, //(Air Quality Index) 전반적인 대기 질의 상태. 1~5(1은 매우 좋음, 5는 매우 나쁨)
    double pm2_5 //초미세먼지 (μg/m^3)
) {

}
