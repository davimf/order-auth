package br.davimf.food.example.repository;

import br.davimf.food.example.entity.Order;
import br.davimf.food.example.entity.OrderStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, UUID> {
  Optional<Order> findFirstByStatusOrderByUpdatedAtAsc(OrderStatus status);

  @Query("SELECT o.status FROM Order o WHERE o.id = :id")
  Optional<OrderStatus> findStatusById(UUID id);
}
