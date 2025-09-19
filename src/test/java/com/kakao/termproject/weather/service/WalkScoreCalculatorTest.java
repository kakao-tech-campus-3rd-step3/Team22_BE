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

  WeatherDetailInternal baseDetail = new WeatherDetailInternal(
      FIXED_TIME, 25.0, 60, WeatherCondition.CLEAR_DAY,
      0, 1.86, 81, 2, 16.0);

  @Test
  public void 테스트1() throws Exception {
    //given
    // 온도 +6 -> 30점 감점. 습도 -10 -> 변동없음. pm2_5 55 나쁨 -> 20점 감점
    WeatherDetailInternal detail = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature() + 6.5, baseDetail.humidity() - 10,
        baseDetail.weather(), baseDetail.precipitationProbability(),
        baseDetail.windSpeed(), baseDetail.windDegree(), 4, 55);

    //when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail);

    //then
    assertThat(walkScore).isEqualTo(DEFAULT_SCORE - 50);
  }

  @Test
  public void 기본점수는_50점() throws Exception {
    //given
    WeatherDetailInternal detail = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature(), baseDetail.humidity(),
        baseDetail.weather(), baseDetail.precipitationProbability(),
        baseDetail.windSpeed(), baseDetail.windDegree(), baseDetail.aqi(), baseDetail.pm2_5());

    //when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail);

    //then
    assertThat(walkScore).isEqualTo(DEFAULT_SCORE);
  }

  @Test
  public void 온도가_25도_초과시_1도당_5점_감점() throws Exception {
    //given
    //기온 +1도 -> 5점 감점
    WeatherDetailInternal detail1 = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature() + 1, baseDetail.humidity(),
        baseDetail.weather(), baseDetail.precipitationProbability(), baseDetail.windSpeed(),
        baseDetail.windDegree(), baseDetail.aqi(), baseDetail.pm2_5());

    //기온 +2.99도 -> 5*2 = 10점 감점
    WeatherDetailInternal detail2 = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature() + 2.99, baseDetail.humidity(),
        baseDetail.weather(), baseDetail.precipitationProbability(), baseDetail.windSpeed(),
        baseDetail.windDegree(), baseDetail.aqi(), baseDetail.pm2_5());

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
    WeatherDetailInternal detail1 = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature(), baseDetail.humidity() + 5,
        baseDetail.weather(), baseDetail.precipitationProbability(), baseDetail.windSpeed(),
        baseDetail.windDegree(), baseDetail.aqi(), baseDetail.pm2_5());

    //습도 +24 -> 24//5 = 4, 4*3=12 -> 12점 감점
    WeatherDetailInternal detail2 = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature(), baseDetail.humidity() + 24,
        baseDetail.weather(), baseDetail.precipitationProbability(), baseDetail.windSpeed(),
        baseDetail.windDegree(), baseDetail.aqi(), baseDetail.pm2_5());

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
    WeatherDetailInternal detail = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature(), baseDetail.humidity(),
        baseDetail.weather(), baseDetail.precipitationProbability(), baseDetail.windSpeed(),
        baseDetail.windDegree(), 5, 77);

    //when
    int walkScore = walkScoreCalculator.calculateWalkScore(detail);

    //then
    assertThat(walkScore).isEqualTo(DEFAULT_SCORE - 40);
  }

  @Test
  public void 대기질_나쁨() throws Exception {
    //given
    //pm2_5 36~75 나쁨 -> 20점 감점
    WeatherDetailInternal detail1 = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature(), baseDetail.humidity(),
        baseDetail.weather(), baseDetail.precipitationProbability(), baseDetail.windSpeed(),
        baseDetail.windDegree(), 4, 75);

    WeatherDetailInternal detail2 = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature(), baseDetail.humidity(),
        baseDetail.weather(), baseDetail.precipitationProbability(), baseDetail.windSpeed(),
        baseDetail.windDegree(), 3, 36);

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
    WeatherDetailInternal detail1 = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature(), baseDetail.humidity(),
        baseDetail.weather(), baseDetail.precipitationProbability(), baseDetail.windSpeed(),
        baseDetail.windDegree(), 3, 35);

    WeatherDetailInternal detail2 = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature(), baseDetail.humidity(),
        baseDetail.weather(), baseDetail.precipitationProbability(), baseDetail.windSpeed(),
        baseDetail.windDegree(), 2, 16);

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
    WeatherDetailInternal detail1 = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature(), baseDetail.humidity(),
        baseDetail.weather(), baseDetail.precipitationProbability(), baseDetail.windSpeed(),
        baseDetail.windDegree(), 2, 15);

    WeatherDetailInternal detail2 = new WeatherDetailInternal(
        baseDetail.dateTime(), baseDetail.temperature(), baseDetail.humidity(),
        baseDetail.weather(), baseDetail.precipitationProbability(), baseDetail.windSpeed(),
        baseDetail.windDegree(), 1, 0);

    //when
    int walkScore1 = walkScoreCalculator.calculateWalkScore(detail1);
    int walkScore2 = walkScoreCalculator.calculateWalkScore(detail2);

    //then
    assertThat(walkScore1).isEqualTo(DEFAULT_SCORE + 10);
    assertThat(walkScore2).isEqualTo(DEFAULT_SCORE + 10);
  }

}