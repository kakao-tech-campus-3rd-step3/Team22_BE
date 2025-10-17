package com.kakao.termproject.history.dto;

import com.kakao.termproject.walk.dto.WalkData;
import java.time.LocalDateTime;

public record HistoryResponse(Long id, LocalDateTime createdAt, WalkData walk) {

}
