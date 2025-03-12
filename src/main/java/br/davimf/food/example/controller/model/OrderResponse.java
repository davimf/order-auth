package br.davimf.food.example.controller.model;

import br.davimf.food.example.entity.OrderStatus;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    UUID userId,
    OrderStatus status,
    String originAddress,
    String deliveryAddress,
    Integer packageAmount) {}
