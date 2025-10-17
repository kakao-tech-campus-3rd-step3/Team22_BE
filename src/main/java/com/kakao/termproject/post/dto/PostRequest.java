package com.kakao.termproject.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequest(
  @NotBlank
  String title,

  @NotBlank
  @Size(min = 10, message = "10자 이상 입력해주세요.")
  String content
) {

}
