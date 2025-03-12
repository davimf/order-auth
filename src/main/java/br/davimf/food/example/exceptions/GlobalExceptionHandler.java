package br.davimf.food.example.exceptions;

import br.davimf.food.example.exceptions.order.NoReadyOrderException;
import br.davimf.food.example.exceptions.order.OrderNotFoundException;
import br.davimf.food.example.exceptions.user.UserNotAuthenticatedException;
import br.davimf.food.example.exceptions.user.UserNotFoundException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred: " + ex.getMessage());
  }

  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    HttpMessageNotReadableException.class,
    InvalidFormatException.class
  })
  public ResponseEntity<String> handleBadRequestException(Exception ex) {
    return ResponseEntity.badRequest().build();
  }

  @ExceptionHandler(NoReadyOrderException.class)
  public ResponseEntity<Void> handleNoContentException(Exception ex) {
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler({UserNotFoundException.class, UserNotAuthenticatedException.class})
  public ResponseEntity<Void> handleForbiddenException(Exception ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<Void> handleNotFoundException(Exception ex) {
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(NotAllowedOperationException.class)
  public ResponseEntity<Void> handleNotAllowedException(Exception ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }

  @ExceptionHandler(NotProcessableOperationException.class)
  public ResponseEntity<Void> handleNotProcessableOperationException(Exception ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
  }
}
