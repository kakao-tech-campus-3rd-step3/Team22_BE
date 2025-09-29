package com.kakao.termproject.walk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakao.termproject.map.dto.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;

public record WalkData(
  @Schema(example = "2010.39")
  @JsonProperty("totalDistance_m")
  Double totalDistance,

  @Schema(example = "1800")
  @JsonProperty("walkingTime_sec")
  Integer walkingTime,

  @JsonProperty("path")
  Coordinate[] coordinates
) {

}
