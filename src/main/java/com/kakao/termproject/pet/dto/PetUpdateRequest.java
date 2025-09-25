package com.kakao.termproject.pet.dto;

import com.kakao.termproject.pet.domain.Gender;
import java.math.BigDecimal;

public record PetUpdateRequest(
  String name,
  String breed,
  Gender gender,
  String birthDate,
  boolean neutralize,
  boolean vaccinated,
  BigDecimal weight,
  String preferredWeather,
  String chronicDisease,
  String preferredPath
) {

}
