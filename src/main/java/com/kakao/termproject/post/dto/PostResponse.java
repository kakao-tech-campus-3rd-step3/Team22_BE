package com.kakao.termproject.post.dto;

import java.time.LocalDateTime;

public record PostResponse(
  Long id,
  String title,
  String content,
  LocalDateTime createdAt,
  LocalDateTime updatedAt,
  Long authorId
) {

}
