package com.kakao.termproject.pet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Pet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Column(nullable = false)
  private String breed;

  @Column(nullable = false)
  private String birthDate;

  @Column(nullable = false)
  private boolean neutralize;

  @Column(nullable = false)
  private boolean vaccinated;

  private String preferredWeather;

  private String chronicDisease;

  private String preferredPath;

  @Column(precision = 5, scale = 2, nullable = false)
  private BigDecimal weight;

  @Builder
  public Pet(
      String name,
      Gender gender,
      String breed,
      String birthDate,
      boolean neutralize,
      boolean vaccinated,
      BigDecimal weight,
      String preferredWeather,
      String preferredPath,
      String chronicDisease){
    this.name = name;
    this.gender = gender;
    this.breed = breed;
    this.birthDate = birthDate;
    this.neutralize = neutralize;
    this.vaccinated = vaccinated;
    this.weight = weight;
    this.preferredWeather = preferredWeather;
    this.preferredPath = preferredPath;
    this.chronicDisease = chronicDisease;
  }

  public void updatePet(
      String name,
      Gender gender,
      String breed,
      String birthDate,
      boolean neutralize,
      boolean vaccinated,
      BigDecimal weight,
      String preferredWeather,
      String preferredPath,
      String chronicDisease
  ) {
    this.name = name;
    this.gender = gender;
    this.breed = breed;
    this.birthDate = birthDate;
    this.neutralize = neutralize;
    this.vaccinated = vaccinated;
    this.weight = weight;
    this.preferredWeather = preferredWeather;
    this.preferredPath = preferredPath;
    this.chronicDisease = chronicDisease;
  }

}
