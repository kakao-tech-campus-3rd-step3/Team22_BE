package com.kakao.termproject.walkscore.policy;

import com.kakao.termproject.walkscore.dto.WalkScoreContext;

public interface WeightPolicy {

  double calculateWeight(WalkScoreContext context);
}
