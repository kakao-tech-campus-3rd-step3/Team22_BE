package com.kakao.termproject.user.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kakao.termproject.pet.domain.Pet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Collection;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @OneToOne
  @JoinColumn(name = "pet_id", unique = true)
  private Pet pet;

  @JsonIgnore
  @Column(name = "activated")
  private boolean activated;

  @Enumerated(EnumType.STRING)
  private Authority authority = Authority.ROLE_USER;

  @Column(name = "refresh_token")
  private String refreshToken;


  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority(authority.name()));
  }

  public void changeUsername(String username) {
     this.username = username;
  }

  public void updateRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public void clearRefreshToken() {
    this.refreshToken = null;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public Member(String email, String username, String password) {
    this.email = email;
    this.username = username;
    this.password = password;
  }

  public void assignPet(Pet pet){
    this.pet = pet;
  }
}
