package com.kakao.termproject.weather.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.kakao.termproject.pet.domain.Gender;
import com.kakao.termproject.pet.domain.Pet;
import com.kakao.termproject.weather.domain.WeatherCondition;
import com.kakao.termproject.weather.dto.WeatherDetailInternal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class WalkScoreCalculatorTest {

  private final WalkScoreCalculator walkScoreCalculator = new WalkScoreCalculator();

  private final static int DEFAULT_SCORE = 50;
  private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 9, 19, 0, 0);
  private static final double HIGH_TEMP_THRESHOLD = 25.0;
  private static final double LOW_TEMP_THRESHOLD = 4.0;
  private static final int HIGH_HUMIDITY_THRESHOLD = 60;
  public static final double PM25_MODERATE = 25.0; // 보통 (16-35) 등급의 대표값

  private WeatherDetailInternal createWeatherDetail(double temp, int humidity, double pm2_5) {
    return new WeatherDetailInternal(
        FIXED_TIME, temp, humidity, WeatherCondition.CLEAR_DAY, 0, 1.86, 81, 2, pm2_5
    );
  }

  private WeatherDetailInternal createWeatherDetailAtTime(
      LocalDateTime dateTime, double temp, int humidity, double pm2_5) {
    return new WeatherDetailInternal(
        dateTime, temp, humidity, WeatherCondition.CLEAR_DAY, 0, 1.86, 81, 2, pm2_5
    );
  }

  private Pet createDog(String breed, String chronicDisease) {
    return new Pet(
        "testdog", Gender.MALE, breed, "2025-09-26", true, true,
        BigDecimal.TEN, "clear", "asphalt", chronicDisease);
  }

  @Test
  public void 여러_패널티_조건이_중첩() throws Exception {
    //given
    // 온도 +6.5 -> 6*5 = 30점 감점.
    // 습도 -10 -> 변동없음.
    // 대기질 pm2_5 55. 나쁨 -> 20점 감점.
    // 총 50점 감점.
    WeatherDetailInternal detail = createWeatherDetail(
        HIGH_TEMP_THRESHOLD + 6.5, HIGH_HUMIDITY_THRESHOLD - 10, 55
    );
    Pet pet = createDog("Unknown", ""); // 개인화 가중치에서는 감점 없음.

    //when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail, pet);

    //then
    assertThat(walkScore).isEqualTo(DEFAULT_SCORE - 50);
  }

  @Test
  public void 기본점수는_50점() throws Exception {
    //given
    WeatherDetailInternal detail = createWeatherDetail(
        HIGH_TEMP_THRESHOLD, HIGH_HUMIDITY_THRESHOLD, PM25_MODERATE
    );
    Pet pet = createDog("Unknown", ""); // 개인화 가중치에서는 감점 없음.

    //when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail, pet);

    //then
    assertThat(walkScore).isEqualTo(DEFAULT_SCORE);
  }

  @Test
  public void 온도가_25도_초과시_1도당_5점_감점() throws Exception {
    //given
    //기온 +1도 -> 5점 감점
    WeatherDetailInternal detail1 = createWeatherDetail(
        HIGH_TEMP_THRESHOLD + 1, HIGH_HUMIDITY_THRESHOLD, PM25_MODERATE
    );
    //기온 +2.99도 -> 5*2 = 10점 감점
    WeatherDetailInternal detail2 = createWeatherDetail(
        HIGH_TEMP_THRESHOLD + 2.99, HIGH_HUMIDITY_THRESHOLD, PM25_MODERATE
    );
    Pet pet = createDog("Unknown", ""); // 개인화 가중치에서는 감점 없음.

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1, pet);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2, pet);

    //then
    assertThat(walkScore1).isEqualTo(DEFAULT_SCORE - 5);
    assertThat(walkScore2).isEqualTo(DEFAULT_SCORE - 10);
  }

  @Test
  public void 온도가_4도_미만시_1도당_5점_감점() throws Exception {
    //given
    //기온 -1도 -> 5점 감점
    WeatherDetailInternal detail1 = createWeatherDetail(
        LOW_TEMP_THRESHOLD - 1, HIGH_HUMIDITY_THRESHOLD, PM25_MODERATE
    );
    //기온 -2.99도 -> 5*2 = 10점 감점
    WeatherDetailInternal detail2 = createWeatherDetail(
        LOW_TEMP_THRESHOLD - 2.99, HIGH_HUMIDITY_THRESHOLD, PM25_MODERATE
    );
    Pet pet = createDog("Unknown", ""); // 개인화 가중치에서는 감점 없음.

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1, pet);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2, pet);

    //then
    assertThat(walkScore1).isEqualTo(DEFAULT_SCORE - 5);
    assertThat(walkScore2).isEqualTo(DEFAULT_SCORE - 10);
  }

  @Test
  public void 습도_60초과시_5당_3점_감점() throws Exception {
    //given
    //습도 +5 -> 3점 감점
    WeatherDetailInternal detail1 = createWeatherDetail(
        HIGH_TEMP_THRESHOLD, HIGH_HUMIDITY_THRESHOLD + 5, PM25_MODERATE
    );
    //습도 +24 -> 24//5 = 4, 4*3=12 -> 12점 감점
    WeatherDetailInternal detail2 = createWeatherDetail(
        HIGH_TEMP_THRESHOLD, HIGH_HUMIDITY_THRESHOLD + 24, PM25_MODERATE
    );
    Pet pet = createDog("Unknown", ""); // 개인화 가중치에서는 감점 없음.

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1, pet);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2, pet);

    //then
    assertThat(walkScore1).isEqualTo(DEFAULT_SCORE - 3);
    assertThat(walkScore2).isEqualTo(DEFAULT_SCORE - 12);
  }

  @Test
  public void 대기질_매우나쁨() throws Exception {
    //given
    //pm2_5 76초과시 매우나쁨 -> 40점 감점
    WeatherDetailInternal detail = createWeatherDetail(
        HIGH_TEMP_THRESHOLD, HIGH_HUMIDITY_THRESHOLD, 77
    );
    Pet pet = createDog("Unknown", ""); // 개인화 가중치에서는 감점 없음.

    //when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail, pet);

    //then
    assertThat(walkScore).isEqualTo(DEFAULT_SCORE - 40);
  }

  @Test
  public void 대기질_나쁨() throws Exception {
    //given
    //pm2_5 36~75 나쁨 -> 20점 감점
    WeatherDetailInternal detail1 = createWeatherDetail(
        HIGH_TEMP_THRESHOLD, HIGH_HUMIDITY_THRESHOLD, 75
    );
    WeatherDetailInternal detail2 = createWeatherDetail(
        HIGH_TEMP_THRESHOLD, HIGH_HUMIDITY_THRESHOLD, 36
    );
    Pet pet = createDog("Unknown", ""); // 개인화 가중치에서는 감점 없음.

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1, pet);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2, pet);

    //then
    assertThat(walkScore1).isEqualTo(DEFAULT_SCORE - 20);
    assertThat(walkScore2).isEqualTo(DEFAULT_SCORE - 20);
  }

  @Test
  public void 대기질_보통() throws Exception {
    //given
    //pm2_5 16~35 보통 -> 변동 없음
    WeatherDetailInternal detail1 = createWeatherDetail(
        HIGH_TEMP_THRESHOLD, HIGH_HUMIDITY_THRESHOLD, 35
    );
    WeatherDetailInternal detail2 = createWeatherDetail(
        HIGH_TEMP_THRESHOLD, HIGH_HUMIDITY_THRESHOLD, 16
    );
    Pet pet = createDog("Unknown", ""); // 개인화 가중치에서는 감점 없음.

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1, pet);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2, pet);

    //then
    assertThat(walkScore1).isEqualTo(DEFAULT_SCORE);
    assertThat(walkScore2).isEqualTo(DEFAULT_SCORE);
  }

  @Test
  public void 대기질_좋음() throws Exception {
    //given
    //pm2_5 0~15 좋음 -> 10점 가점
    WeatherDetailInternal detail1 = createWeatherDetail(
        HIGH_TEMP_THRESHOLD, HIGH_HUMIDITY_THRESHOLD, 15
    );
    WeatherDetailInternal detail2 = createWeatherDetail(
        HIGH_TEMP_THRESHOLD, HIGH_HUMIDITY_THRESHOLD, 0
    );
    Pet pet = createDog("Unknown", ""); // 개인화 가중치에서는 감점 없음.

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1, pet);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2, pet);

    //then
    assertThat(walkScore1).isEqualTo(DEFAULT_SCORE + 10);
    assertThat(walkScore2).isEqualTo(DEFAULT_SCORE + 10);
  }

  @Test
  void 단두종_더운_시간대_가중치() {
    // given
    LocalDateTime hotHour = FIXED_TIME.withHour(14); // 오후 2시
    WeatherDetailInternal detail = createWeatherDetailAtTime(hotHour, 20.0, 50,
        PM25_MODERATE); // 환경점수 = 50. 감점 없음
    Pet pug = createDog("Pug", ""); // 단두종

    double personalizationWeight = 0.2; // 단두종 더운 시간 가중치는 0.2
    int expectedScore = (int) (50 * personalizationWeight); // (0 + 50) * 0.2 = 10

    // when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail, pug);

    // then
    assertThat(walkScore).isEqualTo(expectedScore);
  }

  @Test
  void 비단두종_더운_시간대_가중치() {
    // given
    LocalDateTime hotHour = FIXED_TIME.withHour(14); // 오후 2시
    WeatherDetailInternal detail = createWeatherDetailAtTime(hotHour, 20.0, 50,
        PM25_MODERATE); // 환경점수 = 50. 감점 없음
    Pet maltese = createDog("Maltese", ""); // 단두종

    double personalizationWeight = 0.8; // 비단두종 더운 시간 가중치는 0.8
    int expectedScore = (int) (50 * personalizationWeight); // 50 * 0.8 = 40

    // when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail, maltese);

    // then
    assertThat(walkScore).isEqualTo(expectedScore);
  }

  @Test
  void 단두종_미세먼지_나쁨_가중치() {
    // given
    double pm25Bad = 55.0; // '나쁨' 등급
    WeatherDetailInternal detail = createWeatherDetail(20.0, 50, pm25Bad); // 환경점수 = 50 - 20 = 30
    Pet pug = createDog("Pug", ""); // 단두종

    double personalizationWeight = 0.5;
    int expectedScore = (int) (30 * personalizationWeight); // 30 * 0.5 = 15

    // when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail, pug);

    // then
    assertThat(walkScore).isEqualTo(expectedScore);
  }

  @Test
  void 호흡기질환_미세먼지_나쁨_가중치() {
    // given
    double pm25Bad = 55.0; // '나쁨' 등급
    WeatherDetailInternal detail = createWeatherDetail(20.0, 50, pm25Bad); // 환경점수 = 50 - 20 = 30
    Pet dogWithHeartIssue = createDog("Maltese", "heart"); // 심장/호흡기 질환

    double personalizationWeight = 0.1;
    int expectedScore = (int) (30 * personalizationWeight); // (30) * 0.1 = 3

    // when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail, dogWithHeartIssue);

    // then
    assertThat(walkScore).isEqualTo(expectedScore);
  }

  @Test
  void 단모종_추운날씨_가중치() {
    // given
    double coldTemp = 3.0; // 5도 미만
    WeatherDetailInternal detail = createWeatherDetail(coldTemp, 50, PM25_MODERATE);
    // 저온패널티: (4-3)*5=5, 환경점수 = 50 - 5 = 45
    Pet beagle = createDog("Beagle", ""); // 단모종

    double personalizationWeight = 0.7;
    int expectedScore = (int) (45 * personalizationWeight); // 45 * 0.7 = 31.5 -> 32

    // when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail, beagle);

    // then
    assertThat(walkScore).isEqualTo(expectedScore);
  }
}