package br.davimf.food.example.util.listeners;

import br.davimf.food.example.util.TimestampedEntity;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

public class TimestampListener {
  @PreUpdate
  public void onUpdate(Object entity) {
    if (entity instanceof TimestampedEntity) {
      ((TimestampedEntity) entity).setUpdatedAt(LocalDateTime.now());
    }
  }
}
