package com.kakao.termproject.user.jwt;

import com.kakao.termproject.user.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.accessTokenExpirationTime}")
  private Duration accessTokenExpirationTime;

  @Value("${jwt.refreshTokenExpirationTime}")
  private Duration refreshTokenExpirationTime;


  public String createAccessToken(User user) {
    return Jwts.builder()
      .header()
      .add("typ", "JWT")
      .and()
      .claim("email", user.getEmail())
      .claim("username", user.getUsername())
      .issuedAt(new Date(System.currentTimeMillis()))
      .expiration(Date.from(Instant.now().plus(accessTokenExpirationTime)))
      .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
      .compact();
  }

  public String createRefreshToken(User user) {
    return Jwts.builder()
      .header()
      .add("typ", "JWT")
      .and()
      .claim("email", user.getEmail())
      .claim("username", user.getUsername())
      .issuedAt(new Date(System.currentTimeMillis()))
      .expiration(Date.from(Instant.now().plus(refreshTokenExpirationTime)))
      .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
      .compact();
  }

  public boolean verifyToken(String token) {
    try {
      Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
        .build()
        .parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
