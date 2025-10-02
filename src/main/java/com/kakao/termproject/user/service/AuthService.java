package com.kakao.termproject.user.service;

import com.kakao.termproject.exception.custom.EmailDuplicationException;
import com.kakao.termproject.user.domain.Member;
import com.kakao.termproject.user.dto.LoginRequest;
import com.kakao.termproject.user.dto.RegisterRequest;
import com.kakao.termproject.user.jwt.JwtUtil;
import com.kakao.termproject.user.repository.MemberRepository;
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

  public String register(RegisterRequest request) {

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

    return jwtUtil.createAccessToken(savedMember);
  }


  public String login(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password())
    );

    Member member = (Member) authentication.getPrincipal();

    return jwtUtil.createAccessToken(member);
  }

}
