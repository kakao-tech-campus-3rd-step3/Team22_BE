package com.kakao.termproject.pet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
  private String gender;

  @Column(nullable = false)
  private String birthDate;

  @Column(nullable = false)
  private boolean isNeutered;

  @Column(nullable = false)
  private boolean isVaccinated;

  @Setter
  private String preferredWeather;

  @Setter
  private String chronicDisease;

  @Setter
  private String preferredPath;

  @Column(precision = 5, scale = 2, nullable = false)
  private BigDecimal weight;

  public Pet(String name, String gender, String birthDate, boolean isNeutered, boolean isVaccinated, String preferredWeather, BigDecimal weight){
    this.name = name;
    this.gender = gender;
    this.birthDate = birthDate;
    this.isNeutered = isNeutered;
    this.isVaccinated = isVaccinated;
    this.weight = weight;
  }
}
