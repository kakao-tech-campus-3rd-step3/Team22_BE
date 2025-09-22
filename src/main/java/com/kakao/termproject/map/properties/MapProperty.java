package com.kakao.termproject.map.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "map")
public record MapProperty(String key, String url) {

}
