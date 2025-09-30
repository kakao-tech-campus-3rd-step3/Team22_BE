package com.kakao.termproject.user.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberNameRequest(

    @NotBlank
    String username
) {

}
