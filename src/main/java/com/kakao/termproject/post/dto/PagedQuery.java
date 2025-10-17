package com.kakao.termproject.post.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Sort;

public record PagedQuery(
  @PositiveOrZero
  int page,

  @Positive
  int size,

  Sort.Direction direction,

  String criteria
) {

}
