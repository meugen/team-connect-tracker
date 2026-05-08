package com.ua.teamconnect.tracker.controller;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;

import static org.mockito.Mockito.when;

abstract class AuthorizationControllerTest {

    static final String VALID_TOKEN = "valid-token";
    static final String INVALID_TOKEN = "invalid-token";

    @MockBean
    private JwtDecoder jwtDecoder;

    void setupValidToken(String subject) {
        var jwt = Jwt.withTokenValue(VALID_TOKEN)
            .header("alg", "none")
            .subject(subject)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt);
    }

    void setupValidToken() {
        setupValidToken("test-user");
    }

    void validateUnauthorized(WebTestClient.ResponseSpec spec) {
        spec.expectStatus().isUnauthorized()
            .expectBody()
            .jsonPath("$.message").isNotEmpty()
            .jsonPath("$.status").isEqualTo(401)
            .jsonPath("$.timestamp").isNotEmpty()
            .jsonPath("$.url").isNotEmpty();
    }
}
