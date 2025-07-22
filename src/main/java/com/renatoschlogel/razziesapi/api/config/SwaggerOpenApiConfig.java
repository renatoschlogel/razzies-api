package com.renatoschlogel.razziesapi.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Golden Raspberry Awards API",
                version = "1.0",
                description = "API for querying winners of the Golden Raspberry Awards.",
                contact = @Contact(name = "Renato Welinton Schlogel", email = "renato.s@outlook.com")
        )
)
public class SwaggerOpenApiConfig {
}
