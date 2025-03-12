package br.davimf.food.example.security;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum PublicEndpointList {
  SWAGGER(
      "/v3/api-docs",
      "/v3/api-docs/**",
      "/swagger-resources",
      "/swagger-resources/**",
      "/configuration/ui",
      "/configuration/security",
      "/swagger-ui/**",
      "/webjars/**",
      "/swagger-ui.html"),
  AUTHENTICATION("/api/v1/auth/**");

  private final Set<String> urls;

  PublicEndpointList(String... urls) {
    this.urls = Arrays.stream(urls).collect(Collectors.toSet());
  }

  public static String[] getAll() {
    return Arrays.stream(values()).flatMap(list -> list.getUrls().stream()).toArray(String[]::new);
  }

  public boolean validatePath(String path) {
    return urls.stream().anyMatch(path::contains);
  }

  public Set<String> getUrls() {
    return urls;
  }
}
