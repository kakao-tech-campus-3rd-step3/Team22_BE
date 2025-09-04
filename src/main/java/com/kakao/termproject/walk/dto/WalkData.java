package com.kakao.termproject.walk.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record WalkData(@JsonProperty("routes") Routes routes) {

  public record Routes(@JsonProperty("sections") Sections sections) {

    public record Sections(
      @JsonProperty("departure") Departure departure,
      @JsonProperty("arrival") Arrival arrival,
      @JsonProperty("distance") Integer distance,
      @JsonProperty("duration") Integer duration
    ) {

      public record Departure(
        @JsonProperty("lng") double lng,
        @JsonProperty("lat") double lat
      ) {

      }

      public record Arrival(
        @JsonProperty("lng") double lng,
        @JsonProperty("lat") double lat
      ) {

      }
    }
  }

  @JsonCreator
  public WalkData(@JsonProperty("routes") Routes routes) {
    this.routes = routes;
  }
}
