package br.davimf.food.example.entity.security;

public enum Permission {
  ORDER_CREATE("order:create"),
  ORDER_READ("order:read"),
  ORDER_READ_DELIVERABLE("order:read:deliverable"),
  ORDER_READ_STATUS("order:read:status"),
  ORDER_UPDATE("order:update"),
  ORDER_UPDATE_STATUS("order:update:status"),
  ORDER_CANCEL("order:cancel");

  private final String permission;

  Permission(String permission) {
    this.permission = permission;
  }

  public String getPermission() {
    return permission;
  }
}
