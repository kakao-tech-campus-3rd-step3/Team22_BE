package com.kakao.termproject.walkscore.policy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.walk.domain.Walk;
import com.kakao.termproject.walk.dto.WalkData;
import com.kakao.termproject.walkscore.dto.WalkScoreContext;
import com.kakao.termproject.weather.domain.WeatherCondition;
import com.kakao.termproject.weather.dto.WeatherDetailInternal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonalizationWeightPolicyTest {

  private PersonalizationWeightPolicy personalizationWeightPolicy;

  private static final LocalDateTime HOT_HOUR = LocalDateTime.of(2025, 10, 1, 14, 0);
  private static final LocalDateTime NOT_HOT_HOUR = LocalDateTime.of(2025, 10, 1, 20, 0);
  private static final BigDecimal SMALL_DOG_UNDER_WEIGHT = BigDecimal.valueOf(4);
  private static final BigDecimal SMALL_DOG_NORMAL_WEIGHT = BigDecimal.valueOf(6);

  @BeforeEach
  void setUp() {
    personalizationWeightPolicy = new PersonalizationWeightPolicy();
  }

  @Test
  void 특별한_조건이_없을_때_기본_가중치_1점() {
    // given: 일반적인 상황의 Context
    WalkScoreContext context = createContext(
        createPet("NORMAL_PET"),
        createWalk(1800, 1000),
        createWeather(20.0, 50, 10.0, NOT_HOT_HOUR) // 20시
    );

    // when
    double weight = personalizationWeightPolicy.calculateWeight(context);

    // then
    assertEquals(1.0, weight);
  }

  @Test
  void 단두종이_60분_이상_산책_and_소형견_산책경로_3km_이상() {
    // given
    WalkScoreContext context = createContext(
        createPet("BRACHY_SMALL_PET"),
        createWalk(4000, 5000), // walkingTime > 3600, totaldistance > 3000
        createWeather(20.0, 50, 10.0, NOT_HOT_HOUR)
    );

    // when
    double weight = personalizationWeightPolicy.calculateWeight(context);

    // then: 단두종 60분 이상 -> 0.5, 소형견 3km 이상 -> 0.2
    assertEquals(0.5 * 0.2, weight);
  }

  @Test
  void 단두종이_더운_시간대_and_미세먼지_나쁜_날_산책() {
    // given
    WalkScoreContext context = createContext(
        createPet("BRACHY_SMALL_PET"),
        createWalk(1800, 1000),
        createWeather(20.0, 50, 40.0, HOT_HOUR) // 14시, PM2.5 '나쁨'
    );

    // when
    double weight = personalizationWeightPolicy.calculateWeight(context);

    // then: 단두종 더운 시간대 -> 0.2, 단두종 미세먼지 나쁨 -> 0.5
    assertEquals(0.2 * 0.5, weight);
  }

  @Test
  void 단모종이_추운_날씨에_산책() {
    // given
    WalkScoreContext context = createContext(
        createPet("SHORT_HAIR_PET"),
        createWalk(1800, 1000),
        createWeather(3.0, 50, 10.0, NOT_HOT_HOUR) // 3도
    );

    // when
    double weight = personalizationWeightPolicy.calculateWeight(context);

    // then: 단모종 추운 날씨 -> 0.7
    assertEquals(0.7, weight);
  }

  @Test
  void 비단두종_60분이상_and_저체중_소형견이_매우_긴_거리를_산책() {
    // given
    WalkScoreContext context = createContext(
        createPet("UNDERWEIGHT_SMALL_PET"),
        createWalk(6000, 6000), // 6km
        createWeather(20.0, 50, 10.0, NOT_HOT_HOUR)
    );

    // when
    double weight = personalizationWeightPolicy.calculateWeight(context);

    // then: 비단두종 60분 이상 -> 1.2, 소형견 -> 3km 초과(0.2), 5kg미만 소형견 + 5km초과 -> 0.2
    assertEquals(1.2 * 0.2 * 0.2, weight, 0.001);
  }

  @Test
  void 심장질환이_있고_미세먼지가_나쁜_날_산책() {
    // given
    WalkScoreContext context = createContext(
        createPet("HEART_DISEASE_PET"),
        createWalk(1800, 1000, 5.0), // 경사도 5%
        createWeather(20.0, 50, 50.0, NOT_HOT_HOUR) // PM2.5 '나쁨'
    );

    // when
    double weight = personalizationWeightPolicy.calculateWeight(context);

    // then: 심장질환 and 대기질 나쁨 -> 0.1
    assertEquals(0.1, weight, 0.001);
  }

  @Test
  void 관절질환이_있고_경사가_가파른_경로_산책() {
    // given
    WalkScoreContext context = createContext(
        createPet("JOINT_DISEASE_PET"),
        createWalk(1800, 2000, 12.0), // 경사도 12%
        createWeather(20.0, 50, 10.0, NOT_HOT_HOUR)
    );

    // when
    double weight = personalizationWeightPolicy.calculateWeight(context);

    // then: 관절질환 and 경사도 10도 이상 -> 0
    assertEquals(0.0, weight);
  }

  //헬퍼 메소드
  private WalkScoreContext createContext(Pet pet, Walk walk, WeatherDetailInternal weather) {
    return new WalkScoreContext(weather, pet, walk);
  }

  private Pet createPet(String type) {
    Pet pet = mock(Pet.class);
    switch (type) {
      case "BRACHY_SMALL_PET":
        when(pet.getBreed()).thenReturn("Pug");
        when(pet.getWeight()).thenReturn(SMALL_DOG_NORMAL_WEIGHT);
        when(pet.getChronicDisease()).thenReturn("없음");
        break;
      case "SHORT_HAIR_PET":
        when(pet.getBreed()).thenReturn("Beagle");
        when(pet.getWeight()).thenReturn(SMALL_DOG_NORMAL_WEIGHT);
        when(pet.getChronicDisease()).thenReturn("없음");
        break;
      case "UNDERWEIGHT_SMALL_PET":
        when(pet.getBreed()).thenReturn("Maltese");
        when(pet.getWeight()).thenReturn(SMALL_DOG_UNDER_WEIGHT); // 5kg 미만
        when(pet.getChronicDisease()).thenReturn("없음");
        break;
      case "HEART_DISEASE_PET":
        when(pet.getBreed()).thenReturn("Beagle");
        when(pet.getWeight()).thenReturn(SMALL_DOG_NORMAL_WEIGHT);
        when(pet.getChronicDisease()).thenReturn("heart");
        break;
      case "BRONCHIAL_DISEASE_PET":
        when(pet.getBreed()).thenReturn("Beagle");
        when(pet.getWeight()).thenReturn(SMALL_DOG_NORMAL_WEIGHT);
        when(pet.getChronicDisease()).thenReturn("bronchial");
        break;
      case "JOINT_DISEASE_PET":
        when(pet.getBreed()).thenReturn("Beagle");
        when(pet.getWeight()).thenReturn(SMALL_DOG_NORMAL_WEIGHT);
        when(pet.getChronicDisease()).thenReturn("joint");
        break;
      default: // NORMAL_PET
        when(pet.getBreed()).thenReturn("Unknown");
        when(pet.getChronicDisease()).thenReturn("없음");
        break;
    }
    return pet;
  }

  private Walk createWalk(int walkingTime, double totalDistance) {
    return createWalk(walkingTime, totalDistance, 3.0); // 기본 경사도 3%
  }

  private Walk createWalk(int walkingTime, double totalDistance, double avgOfSlope) {
    Walk walk = mock(Walk.class);
    WalkData walkData = mock(WalkData.class);
    when(walk.getWalk()).thenReturn(walkData);
    when(walkData.walkingTime()).thenReturn(walkingTime);
    when(walkData.totalDistance()).thenReturn(totalDistance);
    when(walk.getAvgOfSlope()).thenReturn(avgOfSlope);
    return walk;
  }

  private WeatherDetailInternal createWeather(double temperature, int humidity, double pm2_5,
      LocalDateTime dateTime) {
    return new WeatherDetailInternal(
        dateTime, temperature, humidity, WeatherCondition.CLEAR_DAY,
        0, 0, 0, 2, pm2_5
    );
  }

}
