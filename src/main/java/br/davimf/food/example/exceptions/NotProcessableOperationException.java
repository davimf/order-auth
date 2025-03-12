package br.davimf.food.example.exceptions;

public class NotProcessableOperationException extends RuntimeException {
  public NotProcessableOperationException() {}

  public NotProcessableOperationException(String message) {
    super(message);
  }
}
