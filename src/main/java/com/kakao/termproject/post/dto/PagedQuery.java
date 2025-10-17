package com.kakao.termproject.post.dto;

import org.springframework.data.domain.Sort;

public record PagedQuery(int page, int size, Sort.Direction direction, String criteria) {

}
