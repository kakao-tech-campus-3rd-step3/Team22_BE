package com.kakao.termproject.weather.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.kakao.termproject.weather.domain.WeatherCondition;
import com.kakao.termproject.weather.dto.WeatherDetailInternal;
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
        FIXED_TIME,
        temp,
        humidity,
        WeatherCondition.CLEAR_DAY, 0, 1.86, 81, 2,
        pm2_5
    );
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

    //when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail);

    //then
    assertThat(walkScore).isEqualTo(DEFAULT_SCORE - 50);
  }

  @Test
  public void 기본점수는_50점() throws Exception {
    //given
    WeatherDetailInternal detail = createWeatherDetail(
        HIGH_TEMP_THRESHOLD, HIGH_HUMIDITY_THRESHOLD, PM25_MODERATE
    );

    //when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail);

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

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2);

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

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2);

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

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2);

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

    //when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail);

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

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2);

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

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2);

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

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2);

    //then
    assertThat(walkScore1).isEqualTo(DEFAULT_SCORE + 10);
    assertThat(walkScore2).isEqualTo(DEFAULT_SCORE + 10);
  }

}