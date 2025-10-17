package com.kakao.termproject.walkscore.policy;

import static org.junit.jupiter.api.Assertions.*;

import com.kakao.termproject.walkscore.dto.WalkScoreContext;
import com.kakao.termproject.weather.domain.WeatherCondition;
import com.kakao.termproject.weather.dto.WeatherDetailInternal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnvironmentalScorePolicyTest {

  private EnvironmentalScorePolicy environmentalScorePolicy;

  @BeforeEach
  void setUp() {
    environmentalScorePolicy = new EnvironmentalScorePolicy();
  }

  @Test
  public void 날씨가_좋을_때_벌점_없이_기본_점수_50점을_반환한다() {
    // given: 날씨가 좋은 상황의 Context 생성
    // 온도 20℃, 습도 50%, 미세먼지 20. pm2.5 20은 보통. 페널티 없음.
    WalkScoreContext context = createContext(20.0, 50, 20);

    // when: 점수 계산 실행
    int score = environmentalScorePolicy.calculateScore(context);

    // then: 기본 점수 50점이 반환되어야 함
    assertEquals(50, score);
  }

  @Test
  public void 고온30도일_때_온도_벌점이_적용된_점수를_반환한다() {
    // given: 온도가 30℃인 상황
    WalkScoreContext context = createContext(30.0, 50, 20);
    // 예상 페널티: (30 - 25) * 5 = 25점

    // when: 점수 계산 실행
    int score = environmentalScorePolicy.calculateScore(context);

    // then: 50 - 25 = 25점
    assertEquals(25, score);
  }

  @Test
  public void 저온0도일_때_온도_벌점이_적용된_점수를_반환한다() {
    // given: 온도가 0℃인 상황
    WalkScoreContext context = createContext(0.0, 50, 20);
    // 예상 페널티: (4 - 0) * 5 = 20점

    // when: 점수 계산 실행
    int score = environmentalScorePolicy.calculateScore(context);

    // then: 50 - 20 = 30점
    assertEquals(30, score);
  }

  @Test
  public void 고습75퍼센트일_때_습도_벌점이_적용된_점수를_반환한다() {
    // given: 습도가 75%인 상황
    WalkScoreContext context = createContext(20.0, 75, 20);
    // 예상 페널티: (int)((75 - 60) / 5) * 3 = (int)(15 / 5) * 3 = 3 * 3 = 9점

    // when: 점수 계산 실행
    int score = environmentalScorePolicy.calculateScore(context);

    // then: 50 - 9 = 41점
    assertEquals(41, score);
  }

  @Test
  public void 미세먼지_나쁨일_때_대기질_벌점이_적용된_점수를_반환한다() {
    // given: 미세먼지 수치가 40 ('나쁨' 등급, 벌점 20점)
    WalkScoreContext context = createContext(20.0, 50, 40);
    // AirQualityGrade.BAD의 벌점은 20점

    // when: 점수 계산 실행
    int score = environmentalScorePolicy.calculateScore(context);

    // then: 50 - 20 = 30점
    assertEquals(30, score);
  }

  @Test
  public void 여러_조건이_복합적일_때_모든_벌점이_누적된_점수를_반환한다() {
    // given: 고온(30도), 고습(75%), 미세먼지 '나쁨'(40) 상황
    WalkScoreContext context = createContext(30.0, 75, 40);
    // 예상 페널티: 온도(25) + 습도(9) + 미세먼지(20) = 54점

    // when: 점수 계산 실행
    int score = environmentalScorePolicy.calculateScore(context);

    // then: 50 - 54 = -4점 (점수가 음수가 될 수도 있음을 확인)
    assertEquals(-4, score);
  }

  @ParameterizedTest
  @CsvSource({
      "25.9, 50, 50",  // 고온 경계값 (페널티 없음)
      "26.0, 50, 45",  // 고온 페널티 -5점
      "3.1, 50, 50",   // 저온 경계값 (페널티 없음)
      "3.0, 50, 45",   // 저온 페널티 -5점
      "20.0, 64, 50",  // 고습 경계값 (페널티 없음)
      "20.0, 65, 47"   // 고습 페널티 -3점
  })
  public void 기온_습도_경계값_시나리오에서_벌점이_정확히_적용되는지_검증한다(double temperature, int humidity,
      int expectedScore) {
    // given: CsvSource로부터 경계값들을 받음
    WalkScoreContext context = createContext(temperature, humidity, 20);

    // when: 점수 계산
    int score = environmentalScorePolicy.calculateScore(context);

    // then: 예상 점수와 일치해야 함
    assertEquals(expectedScore, score);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 60",    //좋음 경계값 +10점
      "15.9, 60", //좋음 경계값 +10점
      "16, 50",   //보통 경계값 +0점
      "35.9, 50", //보통 경계값 +0점
      "36, 30",   //나쁨 경계값 -20점
      "75.9, 30", //나쁨 경계값 -20점
      "76, 10"    // 매우나쁨 경계값 -40점
  })
  public void 대기질_경계값_시나리오에서_벌점이_정확히_적용되는지_검증한다(double pm2_5, int expectedScore) {
    // given: CsvSource로부터 경계값들을 받음
    WalkScoreContext context = createContext(20, 50, pm2_5);

    // when: 점수 계산
    int score = environmentalScorePolicy.calculateScore(context);

    // then: 예상 점수와 일치해야 함
    assertEquals(expectedScore, score);
  }

  //헬퍼 메소드
  private WalkScoreContext createContext(double temperature, int humidity, double pm2_5) {
    final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 9, 19, 0, 0);
    WeatherDetailInternal weatherDetail = new WeatherDetailInternal(
        FIXED_TIME, // 시간은 이 테스트에서 중요하지 않음
        temperature,
        humidity,
        WeatherCondition.CLEAR_DAY,
        0, // 강수확률
        0, // 풍속
        0, // 풍향
        2, // aqi
        pm2_5 // PM2.5
    );

    return new WalkScoreContext(weatherDetail, null, null);
  }
}