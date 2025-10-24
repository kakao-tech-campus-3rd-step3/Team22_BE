package com.kakao.termproject.history.domain;

import com.kakao.termproject.converter.WalkDataConverter;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.walk.dto.WalkData;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class History {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDateTime createdAt;

  @Column(columnDefinition = "TEXT")
  @Convert(converter = WalkDataConverter.class)
  private WalkData walk;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  public History(WalkData walk, Member member) {
    this.createdAt = LocalDateTime.now();
    this.walk = walk;
    this.member = member;
  }

  public boolean isOwner(Member member) {
    if (this.member != null && member != null) {
      return this.member.getId().equals(member.getId());
    }
    return false;
  }
}
