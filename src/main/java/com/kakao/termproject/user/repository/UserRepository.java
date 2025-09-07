package com.kakao.termproject.user.repository;

import com.kakao.termproject.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findUserByEmail(String email);

}
