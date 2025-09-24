package com.kakao.termproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class TermprojectApplication {

  public static void main(String[] args) {
    SpringApplication.run(TermprojectApplication.class, args);
  }

}
