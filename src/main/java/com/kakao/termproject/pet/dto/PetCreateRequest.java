package com.kakao.termproject.pet.dto;

import com.kakao.termproject.pet.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PetCreateRequest(

  @NotBlank
  @Schema(description = "이름", example = "Buddy")
  String name,

  @NotBlank
  @Schema(description = "종", example = "Maltese")
  String breed,

  @NotBlank
  @Schema(description = "성별", example = "MALE")
  Gender gender,

  @NotBlank
  @Schema(description = "출생일", example = "2022-02-22")
  String birthDate,

  @NotNull
  @Schema(description = "중성화여부", example = "true")
  boolean neutralize,

  @NotNull
  @Schema(description = "예방접종여부", example = "true")
  boolean vaccinated,

  @NotNull
  @Schema(description = "몸무게", example = "4")
  BigDecimal weight,

  @Schema(description = "선호 날씨", example = "sun")
  String preferredWeather,

  @Schema(description = "만성 질환", example = "heart")
  String chronicDisease,

  @Schema(description = "선호 길 종류", example = "asphalt")
  String preferredPath
) {

}
