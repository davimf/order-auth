package br.davimf.food.example.entity.security;

import br.davimf.food.example.util.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "authorization_tokens")
public class Token extends TimestampedEntity {
  @Enumerated(EnumType.STRING)
  private TokenType type = TokenType.BEARER;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "user_id", insertable = false, updatable = false)
  private UUID userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserAuthorizations userAuthorizations;

  @Column(unique = true)
  private String token;

  @Column private boolean revoked;
  @Column private boolean expired;

  public Token(UserAuthorizations userAuthorizations, String token) {
    this.userAuthorizations = userAuthorizations;
    this.token = token;
  }

  public Token() {}

  public UserAuthorizations getUserAuthorizations() {
    return userAuthorizations;
  }

  public void setUserAuthorizations(UserAuthorizations userAuthorizations) {
    this.userAuthorizations = userAuthorizations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Token token1 = (Token) o;
    return Objects.equals(id, token1.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public TokenType getType() {
    return type;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public boolean isRevoked() {
    return revoked;
  }

  public void setRevoked(boolean revoked) {
    this.revoked = revoked;
  }

  public boolean isExpired() {
    return expired;
  }

  public void setExpired(boolean expired) {
    this.expired = expired;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }
}
