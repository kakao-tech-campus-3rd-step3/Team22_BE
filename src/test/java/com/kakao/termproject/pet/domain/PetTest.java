package com.kakao.termproject.pet.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.termproject.pet.dto.PetCreateRequest;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class PetTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @DisplayName("Pet 역직렬화 테스트")
  void Pet_역직렬화_테스트코드() throws JsonProcessingException {
    String json = """
        {
        "name": "Buddy",
        "breed": "Maltese",
        "gender": "MALE",
        "birthDate": "2022-02-22",
        "neutralize": true,
        "vaccinated": true,
        "weight": 4,
        "preferredWeather": "sun",
        "chronicDisease": "heart",
        "preferredPath": "asphalt"
        }
        """;

    PetCreateRequest request = objectMapper.readValue(json, PetCreateRequest.class);

    assertThat(request.name()).isEqualTo("Buddy");
    assertThat(request.breed()).isEqualTo("Maltese");
    assertThat(request.gender()).isEqualTo(Gender.MALE);
    assertThat(request.birthDate()).isEqualTo("2022-02-22");
    assertThat(request.neutralize()).isTrue();
    assertThat(request.vaccinated()).isTrue();
    assertThat(request.weight()).isEqualByComparingTo(BigDecimal.valueOf(4));
    assertThat(request.preferredWeather()).isEqualTo("sun");
    assertThat(request.chronicDisease()).isEqualTo("heart");
    assertThat(request.preferredPath()).isEqualTo("asphalt");
  }

}