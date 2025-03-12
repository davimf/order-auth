package br.davimf.food.example;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestClockConfig {
  @Bean
  @Primary
  public MutableClock testClock() {
    return new MutableClock();
  }

  public void advanceTime(Duration duration) {
    testClock().advanceTime(duration);
  }

  public void resetTime() {
    testClock().resetTime();
  }
}
