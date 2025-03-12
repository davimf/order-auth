package br.davimf.food.example.controller.mapper;

import br.davimf.food.example.controller.model.OrderRequest;
import br.davimf.food.example.controller.model.OrderResponse;
import br.davimf.food.example.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

  public Order toEntity(OrderRequest request) {
    Order order = new Order();
    order.setStatus(request.status());
    order.setOriginAddress(request.originAddress());
    order.setDeliveryAddress(request.deliveryAddress());
    order.setPackageAmount(request.packageAmount());
    return order;
  }

  public OrderResponse toResponse(Order order) {
    return new OrderResponse(
        order.getId(),
        order.getUser().getId(),
        order.getStatus(),
        order.getOriginAddress(),
        order.getDeliveryAddress(),
        order.getPackageAmount());
  }
}
