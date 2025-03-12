package br.davimf.food.example.controller;

import br.davimf.food.example.annotations.ExtractUserId;
import br.davimf.food.example.controller.mapper.OrderMapper;
import br.davimf.food.example.controller.model.OrderRequest;
import br.davimf.food.example.controller.model.OrderResponse;
import br.davimf.food.example.controller.model.OrderStatusResponse;
import br.davimf.food.example.entity.OrderStatus;
import br.davimf.food.example.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Operations related to orders")
public class OrderController {
  private final OrderService service;
  private final OrderMapper mapper;

  public OrderController(OrderService service, OrderMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @GetMapping(path = "/next-ready")
  @Operation(summary = "Get first order able to delivery", description = "Retrieves one order")
  @ApiResponse(responseCode = "200", description = "Successful operation with deliverable order")
  @ApiResponse(responseCode = "204", description = "There is not any deliverable order")
  public ResponseEntity<OrderResponse> getDeliverable() {
    return ResponseEntity.ok(mapper.toResponse(service.getDeliverableOrder()));
  }

  @GetMapping(path = "{id}")
  public ResponseEntity<OrderResponse> getOrderById(
      @Parameter(description = "Order ID", example = "123e4567-e89b-12d3-a456-426614174000")
          @PathVariable
          UUID id) {
    return ResponseEntity.ok(mapper.toResponse(service.getById(id)));
  }

  @GetMapping(path = "{id}/status")
  public ResponseEntity<OrderStatusResponse> getStatusByOrderId(
      @Parameter(description = "Order ID", example = "123e4567-e89b-12d3-a456-426614174000")
          @PathVariable
          UUID id) {
    return ResponseEntity.ok(new OrderStatusResponse(service.getStatusById(id)));
  }

  @PostMapping()
  @ExtractUserId
  public ResponseEntity<Void> save(
      @RequestBody @Valid OrderRequest request, @Parameter(hidden = true) UUID userId) {
    var orderId = service.save(mapper.toEntity(request), userId);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(orderId)
            .toUri();

    return ResponseEntity.created(location).build();
  }

  @PutMapping("/{id}/status/{newStatus}")
  public ResponseEntity<Void> updateStatus(
      @Parameter(description = "Order ID", example = "123e4567-e89b-12d3-a456-426614174000")
          @PathVariable
          UUID id,
      @PathVariable OrderStatus newStatus) {
    service.changeStatusById(id, newStatus);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(path = "{id}")
  @ExtractUserId
  public ResponseEntity<OrderResponse> deleteByOrderId(
      @Parameter(description = "Order ID", example = "123e4567-e89b-12d3-a456-426614174000")
          @PathVariable
          UUID id,
      @Parameter(hidden = true) UUID userId) {
    service.deleteById(id, userId);
    return ResponseEntity.ok().build();
  }
}
