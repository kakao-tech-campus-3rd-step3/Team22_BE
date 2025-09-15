package com.kakao.termproject.pet.domain;

import com.kakao.termproject.pet.dto.PetUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
  private String gender;

  @Column(nullable = false)
  private String breed;

  @Column(nullable = false)
  private String birthDate;

  @Column(nullable = false)
  private boolean isNeutered;

  @Column(nullable = false)
  private boolean isVaccinated;

  private String preferredWeather;

  private String chronicDisease;

  private String preferredPath;

  @Column(precision = 5, scale = 2, nullable = false)
  private BigDecimal weight;

  @Builder
  public Pet(
      String name,
      String gender,
      String breed,
      String birthDate,
      boolean isNeutered,
      boolean isVaccinated,
      BigDecimal weight,
      String preferredWeather,
      String preferredPath,
      String chronicDisease){
    this.name = name;
    this.gender = gender;
    this.breed = breed;
    this.birthDate = birthDate;
    this.isNeutered = isNeutered;
    this.isVaccinated = isVaccinated;
    this.weight = weight;
    this.preferredWeather = preferredWeather;
    this.preferredPath = preferredPath;
    this.chronicDisease = chronicDisease;
  }

  public void updatePet(PetUpdateRequest request){
    this.name = request.name();
    this.gender = request.gender();
    this.breed = request.breed();
    this.birthDate = request.birthDate();
    this.isNeutered = request.isNeutered();
    this.isVaccinated = request.isVaccinated();
    this.weight = request.weight();
    this.preferredWeather = request.preferredWeather();
    this.preferredPath = request.preferredPath();
    this.chronicDisease = request.chronicDisease();
  }
}
