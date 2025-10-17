package com.kakao.termproject.walkscore.dto;

import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.walk.domain.Walk;
import com.kakao.termproject.weather.dto.WeatherDetailInternal;

public record WalkScoreContext(
    WeatherDetailInternal weatherDetail,
    Pet pet,
    Walk walk
) {

}
