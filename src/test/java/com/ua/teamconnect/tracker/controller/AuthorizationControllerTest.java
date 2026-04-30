package com.ua.teamconnect.tracker.controller;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;

import static org.mockito.Mockito.when;

abstract class AuthorizationControllerTest {

    protected static final String VALID_TOKEN = "valid-token";

    @MockBean
    private JwtDecoder jwtDecoder;

    protected void setupValidToken() {
        var jwt = Jwt.withTokenValue(VALID_TOKEN)
            .header("alg", "none")
            .subject("test-user")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt);
    }
}
