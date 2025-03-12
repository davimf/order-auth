package br.davimf.food.example.exceptions.user;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException() {}

  public UserNotFoundException(String message) {
    super(message);
  }
}
