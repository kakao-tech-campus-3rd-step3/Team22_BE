package com.kakao.termproject.walkscore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kakao.termproject.walkscore.dto.WalkScoreContext;
import com.kakao.termproject.walkscore.policy.EnvironmentalScorePolicy;
import com.kakao.termproject.walkscore.policy.PersonalizationWeightPolicy;
import com.kakao.termproject.walkscore.policy.RouteScorePolicy;
import com.kakao.termproject.walkscore.service.WalkScoreCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WalkScoreCalculatorTest {

  @InjectMocks
  private WalkScoreCalculator walkScoreCalculator;

  @Mock
  private RouteScorePolicy routeScorePolicy;
  @Mock
  private EnvironmentalScorePolicy environmentalScorePolicy;
  @Mock
  private PersonalizationWeightPolicy personalizationWeightPolicy;

  @Test
  public void 각_정책들의_점수와_가중치를_올바르게_조합하여_최종점수를_계산() throws Exception {
    //given
    when(routeScorePolicy.calculateScore(any(WalkScoreContext.class))).thenReturn(15);
    when(environmentalScorePolicy.calculateScore(any(WalkScoreContext.class))).thenReturn(40);
    when(personalizationWeightPolicy.calculateWeight(any(WalkScoreContext.class))).thenReturn(0.5);

    //when
    int finalScore = walkScoreCalculator.calculateWalkScore(null, null, null);

    //then ((15+40)*0.5 = 27.5 -> 28 )
    assertEquals(28, finalScore);
  }
}