package com.kakao.termproject.history.repository;

import com.kakao.termproject.history.domain.History;
import com.kakao.termproject.user.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {

  List<History> findAllByMember(Member member);

  void deleteAll();
}
