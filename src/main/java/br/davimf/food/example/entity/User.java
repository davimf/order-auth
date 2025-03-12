package br.davimf.food.example.entity;

import static jakarta.persistence.EnumType.STRING;

import br.davimf.food.example.entity.security.Role;
import br.davimf.food.example.entity.security.UserAuthorizations;
import br.davimf.food.example.util.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends TimestampedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne(mappedBy = "user")
  private UserAuthorizations authorizations;

  @Column(nullable = false)
  @Enumerated(STRING)
  private Role role;

  @OneToMany(mappedBy = "user")
  private List<Order> orders;

  @Column(nullable = false, unique = true)
  private String userName;

  public User(UUID id) {
    this.id = id;
  }

  public User(Role role, UUID id, String userName) {
    this.role = role;
    this.id = id;
    this.userName = userName;
  }

  public User() {}

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UserAuthorizations getAuthorizations() {
    return authorizations;
  }

  public void setAuthorizations(UserAuthorizations authorizations) {
    this.authorizations = authorizations;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public void setType(Role role) {
    this.role = role;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void setOrders(List<Order> orders) {
    this.orders = orders;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}
