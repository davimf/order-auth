package br.davimf.food.example.security;

import java.util.Collection;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;

public record AuthUserDetails(
    UUID userId,
    String userName,
    String password,
    Collection<? extends GrantedAuthority> authorities) {}
