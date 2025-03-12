package br.davimf.food.example.exceptions.order;

public class NoReadyOrderException extends RuntimeException {
  public NoReadyOrderException() {}

  public NoReadyOrderException(String message) {
    super(message);
  }
}
