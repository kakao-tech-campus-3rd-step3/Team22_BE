package com.kakao.termproject.user.service;

import com.kakao.termproject.exception.custom.EmailDuplicationException;
import com.kakao.termproject.exception.custom.InvalidTokenException;
import com.kakao.termproject.exception.custom.UserNotFoundException;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.user.dto.LoginRequest;
import com.kakao.termproject.user.dto.RegisterRequest;
import com.kakao.termproject.user.dto.TokenResponse;
import com.kakao.termproject.user.jwt.JwtUtil;
import com.kakao.termproject.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  @Transactional
  public TokenResponse register(RegisterRequest request) {

    if(memberRepository.findUserByEmail(request.email()).isPresent()){
      throw new EmailDuplicationException("중복된 이메일입니다");
    }

    String encodedPassword = passwordEncoder.encode(request.password());

    Member member = new Member(
        request.email(),
        request.username(),
        encodedPassword);

    Member savedMember = memberRepository.save(member);

    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password())
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    String accessToken =  jwtUtil.createAccessToken(savedMember);
    String refreshToken = jwtUtil.createRefreshToken(savedMember);

    return new TokenResponse(accessToken, refreshToken);
  }


  @Transactional
  public TokenResponse login(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password())
    );

    Member member = (Member) authentication.getPrincipal();

    String accessToken =  jwtUtil.createAccessToken(member);
    String refreshToken = jwtUtil.createRefreshToken(member);

    member.updateRefreshToken(refreshToken);
    memberRepository.save(member);

    return new TokenResponse(accessToken, refreshToken);
  }

  public String reissueAccessToken(String refreshToken) {

    if (!jwtUtil.verifyToken(refreshToken)) {
      throw new InvalidTokenException("유효하지 않은 토큰");
    }

    String email = jwtUtil.getEmail(refreshToken);
    Member member = memberRepository.findUserByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없음"));

    if (!refreshToken.equals(member.getRefreshToken())) {
      throw new InvalidTokenException("유효하지 않은 토큰");
    }

    return jwtUtil.createAccessToken(member);
  }

  @Transactional
  public void logout(Member member){
    member.clearRefreshToken();
    memberRepository.save(member);
  }
}
