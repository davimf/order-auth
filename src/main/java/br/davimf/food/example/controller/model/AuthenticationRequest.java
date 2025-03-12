package br.davimf.food.example.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
    @Schema(description = "user name", example = "userName@userName.com") @NotBlank String userName,
    @Schema(description = "password", example = "*******") @NotBlank String password) {}
