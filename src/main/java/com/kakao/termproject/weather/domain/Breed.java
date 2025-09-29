package com.kakao.termproject.weather.domain;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Breed {
  MALTESE("Maltese", SnoutType.NON_BRACHYCEPHALIC, CoatType.LONG_HAIRED, SizeType.SMALL),
  POODLE("Poodle", SnoutType.NON_BRACHYCEPHALIC, CoatType.LONG_HAIRED, SizeType.SMALL),
  POMERANIAN("Pomeranian", SnoutType.NON_BRACHYCEPHALIC, CoatType.LONG_HAIRED, SizeType.SMALL),
  SHIH_TZU("Shih Tzu", SnoutType.BRACHYCEPHALIC, CoatType.LONG_HAIRED, SizeType.SMALL),
  GOLDEN_RETRIEVER("Golden Retriever", SnoutType.NON_BRACHYCEPHALIC, CoatType.LONG_HAIRED, SizeType.LARGE),
  FRENCH_BULLDOG("French Bulldog", SnoutType.BRACHYCEPHALIC, CoatType.SHORT_HAIRED, SizeType.SMALL),
  LABRADOR_RETRIEVER("Labrador Retriever", SnoutType.NON_BRACHYCEPHALIC, CoatType.SHORT_HAIRED, SizeType.LARGE),
  PUG("Pug", SnoutType.BRACHYCEPHALIC, CoatType.SHORT_HAIRED, SizeType.SMALL),
  SAMOYED("Samoyed", SnoutType.NON_BRACHYCEPHALIC, CoatType.LONG_HAIRED, SizeType.LARGE),
  BEAGLE("Beagle", SnoutType.NON_BRACHYCEPHALIC, CoatType.SHORT_HAIRED, SizeType.SMALL),

  //목록에 없는 견종을 위한 기본값
  UNKNOWN("Unknown", SnoutType.UNKNOWN, CoatType.UNKNOWN, SizeType.UNKNOWN);

  private final String breedName;
  private final SnoutType snoutType;
  private final CoatType coatType;
  private final SizeType sizeType;

  //String 값으로 해당하는 Breed Enum을 찾아주는 메소드
  public static Breed fromString(String breedText) {
    return Arrays.stream(Breed.values())
        .filter(breed -> breed.getBreedName().equalsIgnoreCase(breedText))
        .findFirst()
        .orElse(UNKNOWN);
  }

}
