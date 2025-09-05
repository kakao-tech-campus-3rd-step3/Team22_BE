package com.kakao.termproject.user.jwt;

import com.kakao.termproject.user.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

  private final String secretKey;

  public JwtUtil(@Value("${jwt.secret}") String secretKey) {
    this.secretKey = secretKey;
  }

  public String createToken(User user, long expirationTime) {
    return Jwts.builder()
        .header()
        .add("typ", "JWT")
        .and()
        .claim("email", user.getEmail())
        .claim("username", user.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expirationTime)) //30분
        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
        .compact();
  }

  public boolean verifyToken(String token){
    try{
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
