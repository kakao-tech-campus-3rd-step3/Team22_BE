package com.kakao.termproject.user.domain;


import com.kakao.termproject.pet.domain.Pet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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

  public User(String email, String username, String password) {
    this.email = email;
    this.username = username;
    this.password = password;
  }

  public void assignPet(Pet pet){
    this.pet = pet;
  }
}
