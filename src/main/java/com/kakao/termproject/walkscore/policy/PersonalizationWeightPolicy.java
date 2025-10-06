package com.kakao.termproject.walkscore.policy;

import com.kakao.termproject.pet.domain.Breed;
import com.kakao.termproject.pet.domain.CoatType;
import com.kakao.termproject.pet.domain.SizeType;
import com.kakao.termproject.pet.domain.SnoutType;
import com.kakao.termproject.walk.dto.WalkData;
import com.kakao.termproject.weather.domain.AirQualityGrade;
import com.kakao.termproject.walkscore.dto.WalkScoreContext;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class PersonalizationWeightPolicy implements WeightPolicy {

  // 공통 상수
  private static final int LONG_WALK_TIME_THRESHOLD_SECONDS = 3600; // 60분
  private static final int VERY_LONG_DISTANCE_THRESHOLD_M = 5000; // 5km
  private static final int HOT_HOUR_START = 11;
  private static final int HOT_HOUR_END = 16;
  private static final double EXCLUSION_MULTIPLIER = 0.0; // 산책 배제 가중치

  // 코 특성(Snout Type) 관련 상수
  private static final double BRACHY_LONG_WALK_MULTIPLIER = 0.5;      // ID 3-1
  private static final double NON_BRACHY_LONG_WALK_MULTIPLIER = 1.2;    // ID 3-4
  private static final double BRACHY_HOT_HOUR_MULTIPLIER = 0.2;     // ID 3-2
  private static final double NON_BRACHY_HOT_HOUR_MULTIPLIER = 0.8;   // ID 3-5
  private static final double BRACHY_PM_BAD_MULTIPLIER = 0.5;         // ID 3-3

  // 털 종류(Coat Type) 관련 상수
  private static final double COLD_WEATHER_TEMP_THRESHOLD = 5.0;      // 5℃
  private static final double SHORT_HAIR_COLD_MULTIPLIER = 0.7;       // ID 3-9

  // 크기/체중(Size/Weight) 관련 상수
  private static final int SMALL_DOG_LONG_DISTANCE_THRESHOLD_M = 3000; // 3km
  private static final int SMALL_DOG_MEDIUM_DISTANCE_THRESHOLD_M = 1500; // 1.5km
  private static final double SMALL_DOG_LONG_DISTANCE_MULTIPLIER = 0.2;     // ID 3-12
  private static final double SMALL_DOG_MEDIUM_DISTANCE_MULTIPLIER = 1.5;   // ID 3-13
  private static final int SMALL_DOG_UNDERWEIGHT_THRESHOLD_KG = 5;               // 5kg
  private static final double SMALL_DOG_UNDERWEIGHT_VERY_LONG_DISTANCE_MULTIPLIER = 0.2; // ID 3-20
  private static final int LARGE_DOG_OBESE_THRESHOLD_KG = 20;                    // 20kg
  private static final double LARGE_DOG_OBESE_VERY_LONG_DISTANCE_MULTIPLIER = 0.7;     // ID 3-19

  // 지병(Chronic Disease) 관련 상수
  private static final double JOINT_DISEASE_SLOPE_THRESHOLD = 10.0;     // 10%
  private static final double HEART_DISEASE_UPHILL_SLOPE_THRESHOLD = 10.0;  // 10%
  private static final double HEART_DISEASE_PM_BAD_MULTIPLIER = 0.1;      // ID 3-18

  @Override
  public double calculateWeight(WalkScoreContext context) {
    double weight = 1.0;

    weight *= applySnoutTypeWeight(context);
    weight *= applyCoatTypeWeight(context);
    weight *= applySizeTypeWeight(context);
    weight *= applyChronicDiseaseWeight(context);

    return weight;
  }

  // 코 특성에 따른 가중치 계산
  private double applySnoutTypeWeight(WalkScoreContext context) {
    Breed breed = Breed.fromString(context.pet().getBreed());
    SnoutType snoutType = breed.getSnoutType();
    WalkData walkData = context.walk().getWalk();
    double weight = 1.0;

    switch (snoutType) {
      //단두종
      case BRACHYCEPHALIC -> {
        // ID 3-1 산책시간 60분 이상
        if (walkData.walkingTime() > LONG_WALK_TIME_THRESHOLD_SECONDS) {
          weight *= BRACHY_LONG_WALK_MULTIPLIER;
        }
        // ID 3-2 더운 시간대
        int hour = context.weatherDetail().dateTime().getHour();
        if (hour >= HOT_HOUR_START && hour <= HOT_HOUR_END) {
          weight *= BRACHY_HOT_HOUR_MULTIPLIER;
        }
        // ID 3-3 PM2.5 '나쁨' 이상
        if (AirQualityGrade.of(context.weatherDetail().pm2_5()) == AirQualityGrade.BAD) {
          weight *= BRACHY_PM_BAD_MULTIPLIER;
        }
      }
      //비단두종
      case NON_BRACHYCEPHALIC -> {
        // ID 3-4 산책시간 60분 이상
        if (walkData.walkingTime() > LONG_WALK_TIME_THRESHOLD_SECONDS) {
          weight *= NON_BRACHY_LONG_WALK_MULTIPLIER;
        }
        // ID 3-5 더운 시간대
        int hour = context.weatherDetail().dateTime().getHour();
        if (hour >= HOT_HOUR_START && hour <= HOT_HOUR_END) {
          weight *= NON_BRACHY_HOT_HOUR_MULTIPLIER;
        }
      }
    }
    return weight;
  }

  // 털 종류에 따른 가중치 계산
  private double applyCoatTypeWeight(WalkScoreContext context) {
    Breed breed = Breed.fromString(context.pet().getBreed());
    CoatType coatType = breed.getCoatType();
    double weight = 1.0;

    switch (coatType) {
      //단모종
      case SHORT_HAIRED -> {
        // ID 3-9 추운 날씨(5℃ 미만)
        if (context.weatherDetail().temperature() < COLD_WEATHER_TEMP_THRESHOLD) {
          weight *= SHORT_HAIR_COLD_MULTIPLIER;
        }
      }
    }
    return weight;
  }

  // 크기/체중에 따른 가중치 계산
  private double applySizeTypeWeight(WalkScoreContext context) {
    Breed breed = Breed.fromString(context.pet().getBreed());
    SizeType sizeType = breed.getSizeType();
    WalkData walkData = context.walk().getWalk();
    BigDecimal petWeight = context.pet().getWeight();
    double weight = 1.0;

    switch (sizeType) {
      //소형,저활동견
      case SMALL -> {
        if (walkData.totalDistance() > SMALL_DOG_LONG_DISTANCE_THRESHOLD_M) {
          // ID 3-12 산책경로 3km 초과
          weight *= SMALL_DOG_LONG_DISTANCE_MULTIPLIER;
        } else if (walkData.totalDistance() > SMALL_DOG_MEDIUM_DISTANCE_THRESHOLD_M) {
          // ID 3-13 산책경로 1.5km 초과
          weight *= SMALL_DOG_MEDIUM_DISTANCE_MULTIPLIER;
        }
        // ID 3-20 5kg 미만 소형견, 산책경로 5km 초과
        boolean isUnderweight =
            petWeight.compareTo(BigDecimal.valueOf(SMALL_DOG_UNDERWEIGHT_THRESHOLD_KG)) < 0;
        if (isUnderweight && walkData.totalDistance() > VERY_LONG_DISTANCE_THRESHOLD_M) {
          weight *= SMALL_DOG_UNDERWEIGHT_VERY_LONG_DISTANCE_MULTIPLIER;
        }
      }
      //대형견
      case LARGE -> {
        // ID 3-19 20kg 이상 대형견, 산책경로 5km 초과
        boolean isObese = petWeight.compareTo(BigDecimal.valueOf(LARGE_DOG_OBESE_THRESHOLD_KG)) > 0;
        if (isObese && walkData.totalDistance() > VERY_LONG_DISTANCE_THRESHOLD_M) {
          weight *= LARGE_DOG_OBESE_VERY_LONG_DISTANCE_MULTIPLIER;
        }
      }
    }
    return weight;
  }

  // 지병에 따른 가중치 계산
  private double applyChronicDiseaseWeight(WalkScoreContext context) {
    String disease = context.pet().getChronicDisease().toLowerCase();
    WalkData walkData = context.walk().getWalk();
    double avgOfSlope = context.walk().getAvgOfSlope();
    double weight = 1.0;

    switch (disease) {
      //관절 질환
      case "joint" -> {
        // ID 3-15 평균 경사도 10% 이상
        double absAvgSlope = Math.abs(avgOfSlope);
        if (absAvgSlope > JOINT_DISEASE_SLOPE_THRESHOLD) {
          weight *= EXCLUSION_MULTIPLIER;
        }
      }
      //심장/호흡기 질환
      case "heart", "bronchial" -> {
        // ID 3-17 10% 이상 오르막, 산책시간 60분 이상
        if (avgOfSlope > HEART_DISEASE_UPHILL_SLOPE_THRESHOLD
            && walkData.walkingTime() > LONG_WALK_TIME_THRESHOLD_SECONDS) {
          weight *= EXCLUSION_MULTIPLIER;
        }
        // ID 3-18 심장/호흡기 질환, PM2.5 '나쁨' 이상
        if (AirQualityGrade.of(context.weatherDetail().pm2_5()) == AirQualityGrade.BAD) {
          weight *= HEART_DISEASE_PM_BAD_MULTIPLIER;
        }
      }
    }
    return weight;
  }

}
