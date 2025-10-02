package com.kakao.termproject.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MapResponse(
  @Schema(example = "3.2931328460352383")
  double avgOfSlope,

  @Schema(example = "4.487745692403387")
  double maxSlope
) {

}
