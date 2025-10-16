package com.kakao.termproject.walk.domain;

import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.walk.domain.converter.WalkDataConverter;
import com.kakao.termproject.walk.dto.WalkData;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Walk {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT")
  @Convert(converter = WalkDataConverter.class)
  private WalkData walk;

  private Double maxSlope;

  private Double avgOfSlope;

  private LocalDateTime updateDateTime;

  @OneToOne
  @JoinColumn(name = "member_id")
  private Member member;

  public Walk(WalkData walk, Double maxSlope, Double avgOfSlope, Member member) {
    this.walk = walk;
    this.maxSlope = maxSlope;
    this.avgOfSlope = avgOfSlope;
    this.updateDateTime = LocalDateTime.now();
    this.member = member;
  }

  public void updateWalk(WalkData walk, Double maxSlope, Double avgOfSlope) {
    this.walk = walk;
    this.maxSlope = maxSlope;
    this.avgOfSlope = avgOfSlope;
    this.updateDateTime = LocalDateTime.now();
  }

  public void updateSlopes(Double maxSlope, Double avgOfSlope) {
    LocalDateTime now = LocalDateTime.now();

    long weeksDiff = ChronoUnit.WEEKS.between(this.updateDateTime, now);

    if (Math.abs(weeksDiff) >= 2) { // 2주 간격으로 경사도 업데이트
      this.maxSlope = maxSlope;
      this.avgOfSlope = avgOfSlope;
      this.updateDateTime = now;
    }
  }
}
