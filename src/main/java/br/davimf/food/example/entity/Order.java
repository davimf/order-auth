package br.davimf.food.example.entity;

import static jakarta.persistence.EnumType.STRING;

import br.davimf.food.example.util.TimestampedEntity;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order extends TimestampedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  @Enumerated(STRING)
  private OrderStatus status;

  @Column(nullable = false)
  private String originAddress;

  @Column(nullable = false)
  private String deliveryAddress;

  @Column(nullable = false)
  private Integer packageAmount;

  public Order(
      User user,
      OrderStatus status,
      String originAddress,
      String deliveryAddress,
      Integer packageAmount) {
    this.user = user;
    this.status = status;
    this.originAddress = originAddress;
    this.deliveryAddress = deliveryAddress;
    this.packageAmount = packageAmount;
  }

  public Order() {}

  public Order(
      UUID id,
      User user,
      OrderStatus status,
      String originAddress,
      String deliveryAddress,
      Integer packageAmount) {
    this.id = id;
    this.user = user;
    this.status = status;
    this.originAddress = originAddress;
    this.deliveryAddress = deliveryAddress;
    this.packageAmount = packageAmount;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Order order = (Order) o;
    return Objects.equals(id, order.id);
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public boolean isNotRemoved() {
    return status != OrderStatus.CANCELLED;
  }

  public String getOriginAddress() {
    return originAddress;
  }

  public void setOriginAddress(String originAddress) {
    this.originAddress = originAddress;
  }

  public String getDeliveryAddress() {
    return deliveryAddress;
  }

  public void setDeliveryAddress(String deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
  }

  public Integer getPackageAmount() {
    return packageAmount;
  }

  public void setPackageAmount(Integer packageAmount) {
    this.packageAmount = packageAmount;
  }
}
