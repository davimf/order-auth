package br.davimf.food.example.service;

import br.davimf.food.example.controller.model.AuthenticationRequest;
import br.davimf.food.example.controller.model.AuthenticationResponse;
import br.davimf.food.example.entity.security.Token;
import br.davimf.food.example.entity.security.UserAuthorizations;
import br.davimf.food.example.exceptions.user.UserNotFoundException;
import br.davimf.food.example.repository.TokenRepository;
import br.davimf.food.example.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationService(
      UserRepository userRepository,
      TokenRepository tokenRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  @Transactional
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.userName(), request.password()));
    var authorizations =
        userRepository
            .findFirstByUserName(request.userName())
            .orElseThrow(UserNotFoundException::new)
            .getAuthorizations();
    var jwtToken = jwtService.generateToken(authorizations);
    var refreshToken = jwtService.generateRefreshToken(authorizations);
    revokeAllUserTokens(authorizations);
    saveUserToken(authorizations, jwtToken);

    return new AuthenticationResponse(jwtToken, refreshToken);
  }

  private void saveUserToken(UserAuthorizations userAuthorizations, String jwtToken) {
    tokenRepository.save(new Token(userAuthorizations, jwtToken));
  }

  private void revokeAllUserTokens(UserAuthorizations authorizations) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(authorizations.getUser().getId());
    if (validUserTokens.isEmpty()) return;
    validUserTokens.forEach(
        token -> {
          token.setExpired(true);
          token.setRevoked(true);
        });
    tokenRepository.saveAll(validUserTokens);
  }

  @Transactional
  public void refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    final String refreshToken = jwtService.getTokenByAuthorizationHeader(request);
    final String userName = jwtService.extractUsername(refreshToken);
    if (userName != null) {
      var authorizations =
          this.userRepository
              .findFirstByUserName(userName)
              .orElseThrow(UserNotFoundException::new)
              .getAuthorizations();
      if (jwtService.isTokenValid(refreshToken, authorizations)) {
        var accessToken = jwtService.generateToken(authorizations);
        revokeAllUserTokens(authorizations);
        saveUserToken(authorizations, accessToken);
        var authResponse = new AuthenticationResponse(accessToken, refreshToken);
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
