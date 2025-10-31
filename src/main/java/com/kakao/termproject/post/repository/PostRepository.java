package com.kakao.termproject.post.repository;

import com.kakao.termproject.post.domain.Post;
import com.kakao.termproject.user.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

  Page<Post> findAllByMember(Member member, Pageable pageable);
}
