package com.kakao.termproject.user.dto;

public record TokenResponse (
    String accessToken,
    String refreshToken
){

}
