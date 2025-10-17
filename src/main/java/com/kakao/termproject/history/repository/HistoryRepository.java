package com.kakao.termproject.history.repository;

import com.kakao.termproject.history.domain.History;
import com.kakao.termproject.user.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {

  Page<History> findAllByMember(Member member, Pageable pageable);

  void deleteAll();
}
