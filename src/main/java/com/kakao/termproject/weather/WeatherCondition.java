package com.kakao.termproject.weather;

public enum WeatherCondition {
  CLEAR_DAY, CLOUDY_DAY, RAINY_DAY, SNOWY_DAY, WINDY_DAY,
  CLEAR_NIGHT, CLOUDY_NIGHT, RAINY_NIGHT, SNOWY_NIGHT, WINDY_NIGHT;

  public static WeatherCondition from(String main, String pod) {
    boolean isDay = "d".equalsIgnoreCase(pod);

    return switch (main.toLowerCase()) {
      case "clear" -> isDay ? CLEAR_DAY : CLEAR_NIGHT;
      case "clouds" -> isDay ? CLOUDY_DAY : CLOUDY_NIGHT;
      case "rain" -> isDay ? RAINY_DAY : RAINY_NIGHT;
      case "snow" -> isDay ? SNOWY_DAY : SNOWY_NIGHT;
      case "wind" -> isDay ? WINDY_DAY : WINDY_NIGHT;
      default -> throw new IllegalArgumentException("Unknown weather main: " + main);
    };
  }
}
