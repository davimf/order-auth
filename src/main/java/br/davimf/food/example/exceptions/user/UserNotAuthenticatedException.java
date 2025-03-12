package br.davimf.food.example.exceptions.user;

public class UserNotAuthenticatedException extends RuntimeException {
  public UserNotAuthenticatedException() {}

  public UserNotAuthenticatedException(String message) {
    super(message);
  }
}
