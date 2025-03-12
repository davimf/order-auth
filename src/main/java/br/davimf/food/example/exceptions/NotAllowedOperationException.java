package br.davimf.food.example.exceptions;

public class NotAllowedOperationException extends RuntimeException {
  public NotAllowedOperationException() {}

  public NotAllowedOperationException(String message) {
    super(message);
  }
}
