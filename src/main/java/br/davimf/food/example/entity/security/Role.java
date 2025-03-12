package br.davimf.food.example.entity.security;

import static br.davimf.food.example.entity.security.Permission.ORDER_CANCEL;
import static br.davimf.food.example.entity.security.Permission.ORDER_CREATE;
import static br.davimf.food.example.entity.security.Permission.ORDER_READ;
import static br.davimf.food.example.entity.security.Permission.ORDER_READ_DELIVERABLE;
import static br.davimf.food.example.entity.security.Permission.ORDER_READ_STATUS;
import static br.davimf.food.example.entity.security.Permission.ORDER_UPDATE;
import static br.davimf.food.example.entity.security.Permission.ORDER_UPDATE_STATUS;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
  USER(ORDER_CREATE, ORDER_READ, ORDER_READ_STATUS, ORDER_CANCEL, ORDER_UPDATE),
  DRIVER(ORDER_READ_DELIVERABLE, ORDER_READ_STATUS, ORDER_UPDATE_STATUS);

  private final Set<Permission> permissions;

  Role(Permission... permissions) {
    this.permissions = Set.of(permissions);
  }

  public Set<Permission> getPermissions() {
    return permissions;
  }

  public List<SimpleGrantedAuthority> getAuthorities() {
    var authorities =
        getPermissions().stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
            .collect(Collectors.toList());
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
    return Collections.unmodifiableList(authorities);
  }
}
