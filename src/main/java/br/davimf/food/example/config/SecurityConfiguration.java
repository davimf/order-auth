package br.davimf.food.example.config;

import static br.davimf.food.example.entity.security.Permission.ORDER_CANCEL;
import static br.davimf.food.example.entity.security.Permission.ORDER_CREATE;
import static br.davimf.food.example.entity.security.Permission.ORDER_READ;
import static br.davimf.food.example.entity.security.Permission.ORDER_READ_DELIVERABLE;
import static br.davimf.food.example.entity.security.Permission.ORDER_READ_STATUS;
import static br.davimf.food.example.entity.security.Permission.ORDER_UPDATE;
import static br.davimf.food.example.entity.security.Permission.ORDER_UPDATE_STATUS;
import static br.davimf.food.example.entity.security.Role.DRIVER;
import static br.davimf.food.example.entity.security.Role.USER;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import br.davimf.food.example.security.PublicEndpointList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final LogoutHandler logoutHandler;

  public SecurityConfiguration(
      JwtAuthenticationFilter jwtAuthFilter,
      AuthenticationProvider authenticationProvider,
      LogoutHandler logoutHandler) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.authenticationProvider = authenticationProvider;
    this.logoutHandler = logoutHandler;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            req ->
                req.requestMatchers(PublicEndpointList.getAll())
                    .permitAll()
                    .requestMatchers(GET, "/api/v1/orders/next-ready/**")
                    .hasAuthority(ORDER_READ_DELIVERABLE.getPermission())
                    .requestMatchers(GET, "/api/v1/orders/*/status/**")
                    .hasAuthority(ORDER_READ_STATUS.getPermission())
                    .requestMatchers(GET, "/api/v1/orders/**")
                    .hasAuthority(ORDER_READ.getPermission())
                    .requestMatchers(POST, "/api/v1/orders/**")
                    .hasAuthority(ORDER_CREATE.getPermission())
                    .requestMatchers(PUT, "/api/v1/orders/*/status/**")
                    .hasAuthority(ORDER_UPDATE_STATUS.getPermission())
                    .requestMatchers(PUT, "/api/v1/orders/**")
                    .hasAuthority(ORDER_UPDATE.getPermission())
                    .requestMatchers(DELETE, "/api/v1/orders/**")
                    .hasAuthority(ORDER_CANCEL.getPermission())
                    .requestMatchers("/api/v1/orders/**")
                    .hasAnyRole(USER.name(), DRIVER.name())
                    .anyRequest()
                    .authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(
            logout ->
                logout
                    .logoutUrl("/api/v1/auth/logout")
                    .addLogoutHandler(logoutHandler)
                    .logoutSuccessHandler(
                        (request, response, authentication) ->
                            SecurityContextHolder.clearContext()));

    return http.build();
  }
}
