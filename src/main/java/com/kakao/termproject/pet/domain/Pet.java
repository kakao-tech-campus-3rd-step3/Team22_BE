package com.kakao.termproject.pet.domain;

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
  private String breed;

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

  public void changeName(String name) {
    this.name = name;
  }

  public void changeGender(String gender) {
    this.gender = gender;
  }

  public void changeBreed(String breed) {
    this.breed = breed;
  }

  public void changeBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

  public void changeIsNeutered(Boolean isNeutered) {
    this.isNeutered = isNeutered;
  }

  public void changeIsVaccinated(Boolean isVaccinated) {
    this.isVaccinated = isVaccinated;
  }

  public void changeWeight(BigDecimal weight) {
    this.weight = weight;
  }

  public void changePreferredWeather(String preferredWeather) {
    this.preferredWeather = preferredWeather;
  }

  public void changeChronicDisease(String chronicDisease) {
    this.chronicDisease = chronicDisease;
  }

  public void changePreferredPath(String preferredPath) {
    this.preferredPath = preferredPath;
  }
}
