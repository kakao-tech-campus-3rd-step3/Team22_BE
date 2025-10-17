package com.kakao.termproject.post.domain;

import com.kakao.termproject.user.domain.Member;
import jakarta.persistence.Entity;
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
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String content;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  public Post(String title, String content, Member member) {
    this.title = title;
    this.content = content;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.member = member;
  }

  public void updatePost(String title, String content) {
    if (title != null) {
      this.title = title;
    }
    if (content != null) {
      this.content = content;
    }
    this.updatedAt = LocalDateTime.now();
  }
}
