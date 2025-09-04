package com.kakao.termproject.user.service;

import com.kakao.termproject.exception.custom.EmailDuplicationException;
import com.kakao.termproject.exception.custom.InvalidPasswordException;
import com.kakao.termproject.exception.custom.UserNotFoundException;
import com.kakao.termproject.user.domain.User;
import com.kakao.termproject.user.dto.LoginRequest;
import com.kakao.termproject.user.dto.RegisterRequest;
import com.kakao.termproject.user.jwt.JwtUtil;
import com.kakao.termproject.user.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final long accessTokenExpirationTime;
  private final long refreshTokenExpirationTime;

  public UserService(
      UserRepository userRepository,
      JwtUtil jwtUtil,
      @Value("${jwt.accessTokenExpirationTime}") long accessTokenExpirationTime,
      @Value("${jwt.refreshTokenExpitaionTime") long refreshTokenExpirationTime) {
    this.userRepository = userRepository;
    this.jwtUtil = jwtUtil;
    this.accessTokenExpirationTime = accessTokenExpirationTime;
    this.refreshTokenExpirationTime = refreshTokenExpirationTime;
  }

  public String register(RegisterRequest request) {

    if(userRepository.findUserByEmail(request.email()) != null){
      throw new EmailDuplicationException("중복된 이메일입니다");
    }

    String encodedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());

    User user = new User(
        request.email(),
        request.username(),
        encodedPassword);

    userRepository.save(user);
    return jwtUtil.createToken(user, accessTokenExpirationTime);
  }

  public String login(LoginRequest request) {
    User storedUser = userRepository.findUserByEmail(request.email());
    if(storedUser == null){
      throw new UserNotFoundException("사용자를 찾을 수 없습니다");
    }
    if(!BCrypt.checkpw(request.password(), storedUser.getPassword())){
      throw new InvalidPasswordException("비밀번호가 다릅니다");
    }
    return jwtUtil.createToken(storedUser, accessTokenExpirationTime);
  }
}
