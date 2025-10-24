package com.kakao.termproject.image.dto;

import java.util.List;

public record ImageResponse(Long postId, List<String> images) {

}
