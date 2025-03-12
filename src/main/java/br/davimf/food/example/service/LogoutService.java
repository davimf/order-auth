package br.davimf.food.example.service;

import br.davimf.food.example.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutHandler {
  private final JwtService jwtService;
  private final TokenRepository tokenRepository;

  public LogoutService(JwtService jwtService, TokenRepository tokenRepository) {
    this.jwtService = jwtService;
    this.tokenRepository = tokenRepository;
  }

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    var jwt = jwtService.getTokenByAuthorizationHeader(request);
    if (jwt != null) {
      tokenRepository
          .findByToken(jwt)
          .ifPresent(
              storedToken -> {
                storedToken.setExpired(true);
                storedToken.setRevoked(true);
                tokenRepository.save(storedToken);
                SecurityContextHolder.clearContext();
              });
    }
  }
}
