package br.davimf.food.example.config;

import br.davimf.food.example.repository.TokenRepository;
import br.davimf.food.example.security.AuthUserDetails;
import br.davimf.food.example.security.PublicEndpointList;
import br.davimf.food.example.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;

  public JwtAuthenticationFilter(
      JwtService jwtService,
      UserDetailsService userDetailsService,
      TokenRepository tokenRepository) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
    this.tokenRepository = tokenRepository;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    var jwt = jwtService.getTokenByAuthorizationHeader(request);
    if (PublicEndpointList.AUTHENTICATION.validatePath(request.getServletPath()) || jwt == null) {
      filterChain.doFilter(request, response);
      return;
    }

    Optional.ofNullable(jwtService.extractUsername(jwt))
        .filter(userEmail -> SecurityContextHolder.getContext().getAuthentication() == null)
        .map(userDetailsService::loadUserByUsername)
        .filter(userDetails -> jwtService.isTokenValid(jwt, userDetails))
        .flatMap(
            userDetails ->
                tokenRepository
                    .findByToken(jwt)
                    .filter(token -> !token.isExpired() && !token.isRevoked())
                    .map(
                        storedToken -> {
                          var authUserDetails =
                              new AuthUserDetails(
                                  storedToken.getUserId(),
                                  userDetails.getUsername(),
                                  userDetails.getPassword(),
                                  userDetails.getAuthorities());
                          return new UsernamePasswordAuthenticationToken(
                              authUserDetails, null, userDetails.getAuthorities());
                        }))
        .ifPresent(
            authToken -> {
              authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
              SecurityContextHolder.getContext().setAuthentication(authToken);
            });

    filterChain.doFilter(request, response);
  }
}
