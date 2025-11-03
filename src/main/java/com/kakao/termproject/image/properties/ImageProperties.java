package com.kakao.termproject.image.properties;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "image")
public record ImageProperties(
  List<String> allowedExtensions,
  String filePath,
  Long maxFileSize,
  String bucketName
) {

}
