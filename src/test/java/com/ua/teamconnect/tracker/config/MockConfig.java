package com.ua.teamconnect.tracker.config;

import com.ua.teamconnect.tracker.config.secrets.SecretsProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return mock();
    }

    @Bean
    public SecretsProvider secretsProvider() {
        return mock();
    }
}
