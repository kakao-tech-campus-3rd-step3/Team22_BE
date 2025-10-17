package com.kakao.termproject.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.termproject.exception.custom.JsonParseException;
import com.kakao.termproject.walk.dto.WalkData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class WalkDataConverter implements AttributeConverter<WalkData, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(WalkData walkData) {
    try {
      return objectMapper.writeValueAsString(walkData);
    } catch (JsonProcessingException e) {
      throw new JsonParseException("산책 경로 데이터 역직렬화에 실패하였습니다.");
    }
  }

  @Override
  public WalkData convertToEntityAttribute(String walkData) {
    try {
      return objectMapper.readValue(walkData, WalkData.class);
    } catch (JsonProcessingException e) {
      throw new JsonParseException("산책 경로 데이터 직렬화에 실패하였습니다.");
    }
  }
}
