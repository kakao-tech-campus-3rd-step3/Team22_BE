package com.kakao.termproject.walk.repository;

import com.kakao.termproject.walk.domain.Walk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalkRepository extends JpaRepository<Walk, Long> {

}
