package com.kakao.termproject.walkscore.policy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kakao.termproject.walk.domain.Walk;
import com.kakao.termproject.walkscore.dto.WalkScoreContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RouteScorePolicyTest {

  private RouteScorePolicy routeScorePolicy;

  @BeforeEach
  void setUp() {
    routeScorePolicy = new RouteScorePolicy();
  }

  @ParameterizedTest
  @CsvSource({
      "5.9, 20",  //오르막 경계값
      "6.0, 18",  //오르막 페널티 -2점
      "-5.9, 20", //내리막 경계값
      "-6.0, 18", //내리막 페널티 -2점
      "7.5, 16", // 20 - (int)(2.5) * 2 = 16
      "-8.2, 14", // 20 - (int)(3.2) * 2 = 14
  })
  public void 다양한_평균_경사도에_따라_경로_점수가_올바르게_계산되는지_검증한다(double slope, int expectedScore) {
    // given: 특정 경사도 값을 반환하도록 설정된 Context 객체 생성
    WalkScoreContext context = createContextWithSlope(slope);

    // when: 점수 계산 실행
    int score = routeScorePolicy.calculateScore(context);

    // then: 예상된 점수와 실제 계산된 점수가 일치해야 함
    assertEquals(expectedScore, score);
  }

  //헬퍼 메소드
  private WalkScoreContext createContextWithSlope(double avgOfSlope) {
    Walk mockWalk = mock(Walk.class);

    // mockWalk.getAvgOfSlope()가 호출되면 avgOfSlope 값을 반환하도록 설정
    when(mockWalk.getAvgOfSlope()).thenReturn(avgOfSlope);

    return new WalkScoreContext(null, null, mockWalk);
  }
}
