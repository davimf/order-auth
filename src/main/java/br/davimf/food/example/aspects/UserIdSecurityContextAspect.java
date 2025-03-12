package br.davimf.food.example.aspects;

import br.davimf.food.example.exceptions.user.UserNotAuthenticatedException;
import br.davimf.food.example.repository.TokenRepository;
import br.davimf.food.example.service.JwtService;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class UserIdSecurityContextAspect {
  private final JwtService jwtService;
  private final TokenRepository tokenRepository;

  public UserIdSecurityContextAspect(JwtService jwtService, TokenRepository tokenRepository) {
    this.jwtService = jwtService;
    this.tokenRepository = tokenRepository;
  }

  @Around("@annotation(br.davimf.food.example.annotations.ExtractUserId)")
  public Object extractUserId(ProceedingJoinPoint joinPoint) throws Throwable {
    var request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    var jwt =
        Optional.ofNullable(jwtService.getTokenByAuthorizationHeader(request))
            .orElseThrow(UserNotAuthenticatedException::new);
    var userId =
        tokenRepository
            .findByToken(jwt)
            .filter(storedToken -> !storedToken.isExpired() && !storedToken.isRevoked())
            .orElseThrow(UserNotAuthenticatedException::new)
            .getUserId();

    Object[] args =
        Stream.concat(
                Arrays.stream(joinPoint.getArgs()).filter(Objects::nonNull), Stream.of(userId))
            .toArray(Object[]::new);
    return joinPoint.proceed(args);
  }
}
