package com.ua.teamconnect.tracker.controller;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;

import static org.mockito.Mockito.when;

abstract class AuthorizationControllerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    protected void setupValidToken(String token) {
        var jwt = Jwt.withTokenValue(token)
            .header("alg", "none")
            .subject("test-user")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        when(jwtDecoder.decode(token)).thenReturn(jwt);
    }
}
