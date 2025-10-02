package com.kakao.termproject.walk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record WalkResponse(
  @Schema(example = "1")
  Long id,

  @Schema(example = "9.96355771767788")
  Double maxSlope,

  @Schema(example = "5.147757043897205")
  Double avgOfSlope,

  LocalDateTime updatedDateTime,

  WalkData walkData
) {

}
