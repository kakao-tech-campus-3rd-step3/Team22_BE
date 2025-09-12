package com.kakao.termproject.pet.dto;

import java.math.BigDecimal;

public record PetUpdateRequest(
    String name,
    String breed,
    String gender,
    String birthDate,
    Boolean isNeutered,
    Boolean isVaccinated,
    BigDecimal weight,
    String preferredWeather,
    String chronicDisease,
    String preferredPath
) {

}
