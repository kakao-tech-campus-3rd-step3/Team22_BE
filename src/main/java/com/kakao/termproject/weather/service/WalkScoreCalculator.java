package com.kakao.termproject.weather.service;

import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.weather.domain.AirQualityGrade;
import com.kakao.termproject.pet.domain.Breed;
import com.kakao.termproject.pet.domain.CoatType;
import com.kakao.termproject.pet.domain.SnoutType;
import com.kakao.termproject.weather.dto.WeatherDetailInternal;
import org.springframework.stereotype.Component;

@Component
public class WalkScoreCalculator {

  // 환경 점수 계산에 필요한 상수들
  private static final double HIGH_TEMP_THRESHOLD = 25.0;
  private static final double LOW_TEMP_THRESHOLD = 4.0;
  private static final int TEMP_PENALTY_RATE = 5;

  private static final int HIGH_HUMIDITY_THRESHOLD = 60;
  private static final int HUMIDITY_PENALTY_DIVISOR = 5;
  private static final int HUMIDITY_PENALTY_RATE = 3;

  // 개인화 가중치 계산에 필요한 상수들
  private static final double BRACHY_PM_WEIGHT = 0.5;         // ID 3-3
  private static final double HEART_DISEASE_PM_WEIGHT = 0.1;  // ID 3-18
  private static final double SHORT_HAIR_COLD_WEIGHT = 0.7;   // ID 3-9
  private static final double BRACHY_HOT_HOUR_WEIGHT = 0.2;   // ID 3-2
  private static final double NON_BRACHY_HOT_HOUR_WEIGHT = 0.8; // ID 3-5


  public int calculateWalkScore(WeatherDetailInternal detail, Pet pet) {
    int baseRouteScore = 0; // TODO 산책 경로에 대한 점수. 추후 추가 예정.
    int environmentalScore = calculateEnvironmentalScore(detail);
    double personalizationWeight = getPersonalizationWeight(detail, pet);

    //최종 경로 점수(PRS) = (기본 경로 점수 + 환경 변수 점수) * 개인화 가중치
    double calculatedScore = (baseRouteScore + environmentalScore) * personalizationWeight;

    return (int) Math.round(calculatedScore);
  }

  private int calculateEnvironmentalScore(WeatherDetailInternal detail) {
    int score = 50;

    //기온/습도
    double temperature = detail.temperature();
    if (temperature > HIGH_TEMP_THRESHOLD) {
      //25℃ 초과 시 1℃당 -5점
      score -= ((int) (temperature - HIGH_TEMP_THRESHOLD)) * TEMP_PENALTY_RATE;
    }
    if (temperature < LOW_TEMP_THRESHOLD) {
      //4℃ 미만일 경우 1℃당 -5점
      score -= ((int) (LOW_TEMP_THRESHOLD - temperature)) * TEMP_PENALTY_RATE;
    }

    double humidity = detail.humidity();
    if (humidity > HIGH_HUMIDITY_THRESHOLD) {
      //60% 초과 시 5%당 -3점
      int penaltySteps = (int) ((humidity - HIGH_HUMIDITY_THRESHOLD) / HUMIDITY_PENALTY_DIVISOR);
      score -= penaltySteps * HUMIDITY_PENALTY_RATE;
    }

    //대기질
    double pm2_5 = detail.pm2_5();
    AirQualityGrade grade = AirQualityGrade.of(pm2_5);
    score -= grade.getPenalty();

    return score;
  }

  private double getPersonalizationWeight(WeatherDetailInternal detail, Pet pet) {
    Breed breed = Breed.fromString(pet.getBreed());
    String disease = pet.getChronicDisease();

    double weight = 1.0;

    // ID 3-2, 3-5: 더운 시간대 (오전 11시 ~ 오후 4시) 가중치
    int hour = detail.dateTime().getHour();
    if (hour >= 11 && hour <= 16) {
      if (breed.getSnoutType() == SnoutType.BRACHYCEPHALIC) {
        weight *= BRACHY_HOT_HOUR_WEIGHT;
      } else {
        weight *= NON_BRACHY_HOT_HOUR_WEIGHT;
      }
    }

    // ID 3-9: 추운 날씨(5도 미만) + 단모종 가중치
    if (detail.temperature() < 5 && breed.getCoatType() == CoatType.SHORT_HAIRED) {
      weight *= SHORT_HAIR_COLD_WEIGHT;
    }

    // ID 3-3, 3-18: 미세먼지 '나쁨' 이상일 때 가중치
    if (AirQualityGrade.of(detail.pm2_5()) == AirQualityGrade.BAD) {
      if (breed.getSnoutType() == SnoutType.BRACHYCEPHALIC) {
        weight *= BRACHY_PM_WEIGHT;
      }
      if (disease.equalsIgnoreCase("heart")) {
        weight *= HEART_DISEASE_PM_WEIGHT;
      }
    }

    return weight;
  }

}
