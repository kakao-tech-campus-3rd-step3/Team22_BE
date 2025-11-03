package com.kakao.termproject.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequest(
  @NotBlank
  @Size(min = 1, max = 20, message = "제목은 최대 20글자까지 입력 가능합니다.")
  String title,

  @NotBlank
  @Size(min = 10, max = 250, message = "내용은 10자 이상 250자 이하로 입력해주세요.")
  String content
) {

}
