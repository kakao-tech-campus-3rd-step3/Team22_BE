package com.kakao.termproject.walk.domain;

import com.kakao.termproject.user.domain.Member;
import jakarta.persistence.Column;
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
  private String walk;

  private Double maxSlope;

  private Double avgOfSlope;

  private LocalDateTime updateDateTime;

  @OneToOne
  @JoinColumn(name = "member_id")
  private Member member;

  public Walk(String walk, Double maxSlope, Double avgOfSlope, LocalDateTime updateDateTime) {
    this.walk = walk;
    this.maxSlope = maxSlope;
    this.avgOfSlope = avgOfSlope;
    this.updateDateTime = updateDateTime;
  }

  public void updateSlopes(Double maxSlope, Double avgOfSlope, LocalDateTime updateDateTime) {
    long monthDiff = ChronoUnit.WEEKS.between(this.updateDateTime, LocalDateTime.now());

    if (Math.abs(monthDiff) >= 2) { // 2주 간격으로 경사도 업데이트
      this.maxSlope = maxSlope;
      this.avgOfSlope = avgOfSlope;
      this.updateDateTime = updateDateTime;
    }
  }
}
