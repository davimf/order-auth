package br.davimf.food.example;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class MutableClock extends Clock {

  private final Clock systemClock = Clock.systemUTC();
  private Duration offset = Duration.ZERO;

  @Override
  public ZoneId getZone() {
    return systemClock.getZone();
  }

  @Override
  public Clock withZone(ZoneId zone) {
    return new MutableClock().withOffset(offset).withZone(zone);
  }

  @Override
  public Instant instant() {
    return systemClock.instant().plus(offset);
  }

  public MutableClock withOffset(Duration offset) {
    this.offset = offset;
    return this;
  }

  public void advanceTime(Duration duration) {
    offset = offset.plus(duration);
  }

  public void resetTime() {
    offset = Duration.ZERO;
  }
}
