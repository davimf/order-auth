package br.davimf.food.example.exceptions.order;

public class OrderNotFoundException extends RuntimeException {
  public OrderNotFoundException() {}

  public OrderNotFoundException(String message) {
    super(message);
  }
}
