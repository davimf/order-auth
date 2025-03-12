package br.davimf.food.example.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info =
        @Info(
            contact = @Contact(name = "Davi Meireles", url = "http://localhost:8081"),
            description = "OpenApi documentation for Order Auth API Example",
            title = "OpenApi specification for Order Auth API Example - davimf",
            version = "1.0",
            license =
                @License(
                    name = "CC0 1.0 Universal",
                    url = "https://creativecommons.org/publicdomain/zero/1.0/"),
            termsOfService = "Terms of service"),
    servers = {
      @Server(description = "Local ENV", url = "http://localhost:8081"),
    },
    security = {@SecurityRequirement(name = "bearerAuth")})
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT auth description",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {}
