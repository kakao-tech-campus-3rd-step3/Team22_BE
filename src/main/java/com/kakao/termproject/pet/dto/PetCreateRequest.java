package com.kakao.termproject.pet.dto;

import com.kakao.termproject.pet.domain.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PetCreateRequest(

  @NotBlank
  String name,

  @NotBlank
  String breed,

  @NotBlank
  Gender gender,

  @NotBlank
  String birthDate,

  @NotNull
  boolean isNeutered,

  @NotNull
  boolean isVaccinated,

  @NotNull
  BigDecimal weight,

  String preferredWeather,
  String chronicDisease,
  String preferredPath
) {

}
