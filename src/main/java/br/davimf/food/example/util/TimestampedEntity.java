package br.davimf.food.example.util;

import br.davimf.food.example.util.listeners.TimestampListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(TimestampListener.class)
public class TimestampedEntity {
  @Column(nullable = false, insertable = false)
  private LocalDateTime updatedAt;

  public void preUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
