package com.kakao.termproject.weather.service;

import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.walk.domain.Walk;
import com.kakao.termproject.walk.dto.WalkData;
import com.kakao.termproject.weather.domain.AirQualityGrade;
import com.kakao.termproject.pet.domain.Breed;
import com.kakao.termproject.pet.domain.CoatType;
import com.kakao.termproject.pet.domain.SnoutType;
import com.kakao.termproject.weather.dto.WeatherDetailInternal;
import java.math.BigDecimal;
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

  // 경로 점수 계산에 필요한 상수들
  final double SLOPE_THRESHOLD = 5.0; // 점수 차감이 시작되는 경사도 기준 (5%)
  final int SLOPE_PENALTY_RATE = 2; // 경사도가 1% 증가할 때마다 차감될 점수

  public int calculateWalkScore(WeatherDetailInternal detail, Pet pet, Walk walk) {
    int baseRouteScore = calculateBaseRouteScore(walk);
    int environmentalScore = calculateEnvironmentalScore(detail);
    double personalizationWeight = getPersonalizationWeight(detail, pet, walk);

    //최종 경로 점수(PRS) = (기본 경로 점수 + 환경 변수 점수) * 개인화 가중치
    double calculatedScore = (baseRouteScore + environmentalScore) * personalizationWeight;

    return (int) Math.round(calculatedScore);
  }

  private int calculateBaseRouteScore(Walk walk) {
    int score = 20; //기본 점수

    double avgOfSlope = walk.getAvgOfSlope();
    double absAvgSlope = Math.abs(avgOfSlope);

    //평균 경사도 5% 미만: 20점,이후 1% 증가 시 -2점
    if (absAvgSlope > SLOPE_THRESHOLD) {
      score -= ((int) (absAvgSlope - SLOPE_THRESHOLD)) * SLOPE_PENALTY_RATE;
    }

    return score;
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

  private double getPersonalizationWeight(WeatherDetailInternal detail, Pet pet, Walk walk) {
    Breed breed = Breed.fromString(pet.getBreed());
    WalkData walkData = walk.getWalk();

    double weight = 1.0;

    switch (breed.getSnoutType()) {
      //단두종
      case BRACHYCEPHALIC -> {
        // ID 3-1 산책시간 60분 이상
        if (walkData.walkingTime() > 3600) {
          weight *= 0.5;
        }
        // ID 3-2 더운 시간대
        int hour = detail.dateTime().getHour();
        if (hour >= 11 && hour <= 16) {
          weight *= BRACHY_HOT_HOUR_WEIGHT;
        }
        // ID 3-3 PM2.5 '나쁨' 이상
        if (AirQualityGrade.of(detail.pm2_5()) == AirQualityGrade.BAD) {
          weight *= BRACHY_PM_WEIGHT;
        }
      }
      //비단두종
      case NON_BRACHYCEPHALIC -> {
        // ID 3-4 산책시간 60분 이상
        if (walkData.walkingTime() > 3600) {
          weight *= 1.2;
        }
        // ID 3-5 더운 시간대
        int hour = detail.dateTime().getHour();
        if (hour >= 11 && hour <= 16) {
          weight *= NON_BRACHY_HOT_HOUR_WEIGHT;
        }
      }
    }

    switch (breed.getCoatType()) {
      //단모종
      case SHORT_HAIRED -> {
        // ID 3-9 추운 날씨(5℃ 미만)
        if (detail.temperature() < 5) {
          weight *= SHORT_HAIR_COLD_WEIGHT;
        }
      }
    }

    switch (breed.getSizeType()) {
      //소형,저활동견
      case SMALL -> {
        if (walkData.totalDistance() > 3000) {
          // ID 3-12 산책경로 3km 초과
          weight *= 0.2;
        } else if (walkData.totalDistance() > 1500) {
          // ID 3-13 산책경로 1.5km 초과
          weight *= 1.5;
        }
        // ID 3-20 5kg 미만 소형견, 산책경로 5km 초과
        BigDecimal petWeight = pet.getWeight();
        boolean isUnderweight = petWeight.compareTo(BigDecimal.valueOf(5)) < 0;
        if (isUnderweight && walkData.totalDistance() > 5000) {
          weight *= 0.2;
        }
      }
      //대형견
      case LARGE -> {
        // ID 3-19 20kg 이상 대형견, 산책경로 5km 초과
        BigDecimal petWeight = pet.getWeight();
        boolean isObese = petWeight.compareTo(BigDecimal.valueOf(20)) > 0;
        if (isObese && walkData.totalDistance() > 5000) {
          weight *= 0.7;
        }
      }
    }

    //지병/건강
    String disease = pet.getChronicDisease().toLowerCase();
    switch (disease) {
      case "joint" -> {
        // ID 3-15 관절 질환, 평균 경사도 10% 이상
        double absAvgSlope = Math.abs(walk.getAvgOfSlope());
        if (absAvgSlope > 10) {
          weight *= 0;
        }
      }
      case "heart", "bronchial" -> {
        // ID 3-17 심장/호흡기 질환, 10% 이상 오르막, 산책시간 60분 이상
        double avgOfSlope = walk.getAvgOfSlope();
        if (avgOfSlope > 10 && walkData.walkingTime() > 3600) {
          weight *= 0;
        }
        // ID 3-18 심장/호흡기 질환, PM2.5 '나쁨' 이상
        if (AirQualityGrade.of(detail.pm2_5()) == AirQualityGrade.BAD) {
          weight *= HEART_DISEASE_PM_WEIGHT;
        }
      }
    }

    return weight;
  }

}
