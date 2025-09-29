package com.kakao.termproject.user.repository;

import com.kakao.termproject.user.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findUserByEmail(String email);

}
