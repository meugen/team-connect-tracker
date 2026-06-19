package com.ua.teamconnect.tracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;

abstract class AuthorizationControllerTest {

    static final String VALID_TOKEN = "valid-token";
    static final String INVALID_TOKEN = "invalid-token";

    @MockitoBean
    private JwtDecoder jwtDecoder;

    void setupValidToken(String subject, String role) {
        var jwt = Jwt.withTokenValue(VALID_TOKEN)
            .header("alg", "none")
            .subject(subject)
            .claim("roles", List.of("ROLE_" + role))
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt);
    }

    void setupValidToken(String subject) {
        setupValidToken(subject, "ENGINEER");
    }

    void setupValidToken() {
        setupValidToken("test-user");
    }

    void validateUnauthorized(WebTestClient.ResponseSpec spec) {
        validateHttpStatus(spec, HttpStatus.UNAUTHORIZED);
    }

    void validateBadRequest(WebTestClient.ResponseSpec spec) {
        validateHttpStatus(spec, HttpStatus.BAD_REQUEST);
    }

    void validateNotFound(WebTestClient.ResponseSpec spec) {
        validateHttpStatus(spec, HttpStatus.NOT_FOUND);
    }
    
    void validateNoContent(WebTestClient.ResponseSpec spec) {
        spec.expectStatus().isNoContent().expectBody().isEmpty();
    }

    void validatePayloadTooLarge(WebTestClient.ResponseSpec spec) {
        validateHttpStatus(spec, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    private void validateHttpStatus(WebTestClient.ResponseSpec spec, HttpStatus status) {
        spec.expectStatus().isEqualTo(status)
            .expectBody()
            .jsonPath("$.message").isNotEmpty()
            .jsonPath("$.status").isEqualTo(status.value())
            .jsonPath("$.timestamp").isNotEmpty()
            .jsonPath("$.url").isNotEmpty();
    }
}
