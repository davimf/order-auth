package br.davimf.food.example.service;

import static br.davimf.food.example.entity.OrderStatus.CANCELLED;
import static br.davimf.food.example.entity.OrderStatus.DELIVERED;
import static br.davimf.food.example.entity.OrderStatus.ORDERED;

import br.davimf.food.example.entity.Order;
import br.davimf.food.example.entity.OrderStatus;
import br.davimf.food.example.entity.User;
import br.davimf.food.example.exceptions.NotAllowedOperationException;
import br.davimf.food.example.exceptions.NotProcessableOperationException;
import br.davimf.food.example.exceptions.order.NoReadyOrderException;
import br.davimf.food.example.exceptions.order.OrderNotFoundException;
import br.davimf.food.example.repository.OrderRepository;
import java.util.UUID;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
  private final OrderRepository repository;

  public OrderService(OrderRepository repository) {
    this.repository = repository;
  }

  public Order getDeliverableOrder() {
    return repository
        .findFirstByStatusOrderByUpdatedAtAsc(OrderStatus.READY)
        .orElseThrow(NoReadyOrderException::new);
  }

  public Order getById(@NonNull UUID id) {
    return repository
        .findById(id)
        .filter(Order::isNotRemoved)
        .orElseThrow(OrderNotFoundException::new);
  }

  public OrderStatus getStatusById(@NonNull UUID id) {
    return repository.findStatusById(id).orElseThrow(OrderNotFoundException::new);
  }

  @Transactional
  public UUID save(@NonNull Order order, @NonNull UUID userId) {
    order.setUser(new User(userId));
    return repository.save(order).getId();
  }

  @Transactional
  public void deleteById(@NonNull UUID id, @NonNull UUID userId) {
    Order order = repository.findById(id).orElseThrow(OrderNotFoundException::new);
    if (!ORDERED.equals(order.getStatus())) throw new NotProcessableOperationException();
    if (!userId.equals(order.getUser().getId())) throw new NotAllowedOperationException();
    order.setStatus(CANCELLED);
    repository.save(order);
  }

  @Transactional
  public void changeStatusById(@NonNull UUID id, @NonNull OrderStatus status) {
    Order order = repository.findById(id).orElseThrow(OrderNotFoundException::new);
    if (CANCELLED.equals(status) || DELIVERED.equals(order.getStatus()))
      throw new NotProcessableOperationException();
    order.setStatus(status);
    repository.save(order);
  }
}
