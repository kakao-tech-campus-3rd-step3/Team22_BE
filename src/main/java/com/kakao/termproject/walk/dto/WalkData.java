package com.kakao.termproject.walk.dto;

public record WalkData(Routes routes) {

  record Routes(Sections sections) {

    record Sections(double[] departure, double[] arrival, Integer distance, Integer duration) {

    }
  }
}
