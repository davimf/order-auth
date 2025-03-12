package br.davimf.food.example.controller.model;

import br.davimf.food.example.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderRequest(
    @Schema(
            description = "Order Status",
            example = "ORDERED",
            allowableValues = {"ORDERED", "READY", "DELIVERING", "DELIVERED", "CANCELLED"})
        @NotNull
        OrderStatus status,
    @Schema(description = "Origin Address", example = "123 Main St") @NotBlank String originAddress,
    @Schema(description = "Delivery Address", example = "456 Oak Ave") @NotBlank
        String deliveryAddress,
    @Schema(description = "Package Amount", example = "3", minimum = "1") @Positive
        int packageAmount) {}
