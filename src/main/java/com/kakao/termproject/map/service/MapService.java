package com.kakao.termproject.map.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.termproject.exception.custom.JsonParseException;
import com.kakao.termproject.map.dto.Coordinate;
import com.kakao.termproject.map.dto.MapResponse;
import com.kakao.termproject.map.properties.MapProperties;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapService {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final MapProperties mapProperties;

  public MapResponse getFitness(Coordinate[] coordinates) {
    StringBuilder parameter = new StringBuilder();
    List<Double> distances = getDistancesAndBuildParameter(coordinates, parameter);

    JsonNode response = getResponse(parameter.toString());
    List<Double> elevations = getElevations(response);

    return calcAvgAndMaxSlope(elevations, distances);
  }

  private JsonNode getResponse(String parameter) {
    URI uri = UriComponentsBuilder
      .fromUriString(mapProperties.url())
      .queryParam("locations", parameter)
      .queryParam("key", mapProperties.key())
      .build()
      .toUri();

    String response = restTemplate.getForObject(uri, String.class);

    try {
      return objectMapper.readTree(response)
        .get("results");
    } catch (JsonProcessingException e) {
      throw new JsonParseException("Google Api 반환 데이터 직렬화에 실패하였습니다.");
    }
  }

  private List<Double> getElevations(JsonNode data) {
    List<Double> elevations = new ArrayList<>();

    for (JsonNode elevation : data) {
      elevations.add(elevation.get("elevation").asDouble());
    }

    return elevations;
  }

  private List<Double> getDistancesAndBuildParameter(Coordinate[] coordinates,
    StringBuilder parameter) {
    List<Double> distances = new ArrayList<>();

    for (int i = 0; i < coordinates.length - 1; i++) {
      distances.add(haversineDistance(coordinates[i], coordinates[i + 1]));

      parameter.append(coordinates[i].lat())
        .append(",")
        .append(coordinates[i].lng())
        .append("|");
    }

    parameter.append(coordinates[coordinates.length - 1].lat())
      .append(",")
      .append(coordinates[coordinates.length - 1].lng());

    return distances;
  }

  private MapResponse calcAvgAndMaxSlope(List<Double> elevations, List<Double> distances) {
    double maxSlope = 0.0;
    double sumSlope = 0.0;

    for (int i = 0; i < elevations.size() - 1; i++) {

      if (Double.compare(distances.get(i), 0.0) <= 0) {
        continue;
      }

      double currentSlope = (elevations.get(i + 1) - elevations.get(i)) / distances.get(i);

      currentSlope = Math.abs(currentSlope);

      sumSlope += currentSlope;

      if (currentSlope > maxSlope) {
        maxSlope = currentSlope;
      }
    }

    double avgOfSlope = sumSlope;

    if (elevations.size() > 1) {
      avgOfSlope /= (elevations.size() - 1);
    }

    avgOfSlope *= 100;
    maxSlope *= 100;

    return new MapResponse(avgOfSlope, maxSlope);
  }

  private Double haversineDistance(Coordinate location1, Coordinate location2) {
    final int RADIUS = 6371000; // earth radius meter

    double latDistance = Math.toRadians(location2.lat() - location1.lat());
    double lonDistance = Math.toRadians(location2.lng() - location1.lng());

    double sinLatHalfSquared = Math.sin(latDistance / 2) * Math.sin(latDistance / 2);
    double sinLonHalfSquared = Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

    double location1Radians = Math.toRadians(location1.lat());
    double location2Radians = Math.toRadians(location2.lat());

    double a = sinLatHalfSquared + Math.cos(location1Radians)
      * Math.cos(location2Radians) * sinLonHalfSquared;

    double centralAngle = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return RADIUS * centralAngle; // distance between location1 and location2
  }
}
