package com.ua.teamconnect.tracker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        var components = new Components()
            .addSecuritySchemes(SECURITY_SCHEME, new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .name(SECURITY_SCHEME)
                .bearerFormat("JWT")
                .description("JWT Access Token")
            );
        var securityItem = new SecurityRequirement()
            .addList(SECURITY_SCHEME);
        return new OpenAPI().components(components)
            .addSecurityItem(securityItem);
    }
}
