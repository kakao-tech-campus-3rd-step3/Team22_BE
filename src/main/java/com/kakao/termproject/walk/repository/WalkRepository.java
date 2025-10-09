package com.kakao.termproject.walk.repository;

import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.walk.domain.Walk;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalkRepository extends JpaRepository<Walk, Long> {

  Optional<Walk> findByMember(Member member);
}
