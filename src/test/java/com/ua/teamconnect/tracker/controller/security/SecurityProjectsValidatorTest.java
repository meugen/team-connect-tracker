package com.ua.teamconnect.tracker.controller.security;

import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.exception.NotFoundException;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityProjectsValidatorTest {

    private UserRepository userRepository;
    private UserProjectRepository userProjectRepository;
    private SecurityProjectsValidator securityProjectsValidator;
    private MethodSecurityExpressionOperations operations;

    @BeforeEach
    void setUp() {
        userRepository = mock();
        userProjectRepository = mock();
        securityProjectsValidator = new SecurityProjectsValidator(userRepository, userProjectRepository);
        operations = mock();
    }

    @Test
    void validate_roleEngineer_false() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(operations.hasAnyRole("HR", "ADMIN")).thenReturn(false);
        when(operations.hasRole("PM")).thenReturn(false);

        var result = securityProjectsValidator.validate(1, operations);

        verify(userRepository, never()).findByEmail(any());
        verify(userProjectRepository, never()).findProjectIdsByUserIdAndNow(any(), any());
        assertFalse(result);
    }

    @Test
    void validate_roleHr_true() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(operations.hasAnyRole("HR", "ADMIN")).thenReturn(true);

        var result = securityProjectsValidator.validate(1, operations);

        verify(operations, never()).hasRole("PM");
        verify(userRepository, never()).findByEmail(any());
        verify(userProjectRepository, never()).findProjectIdsByUserIdAndNow(any(), any());
        assertTrue(result);
    }

    @Test
    void validate_rolePmNoCommonProjects_false() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(operations.hasAnyRole("HR", "ADMIN")).thenReturn(false);
        when(operations.hasRole("PM")).thenReturn(true);
        var pmUser = mock(User.class);
        when(pmUser.getId()).thenReturn(2);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(pmUser));
        when(userProjectRepository.findProjectIdsByUserIdAndNow(eq(1), any()))
            .thenReturn(Set.of(1, 2));
        when(userProjectRepository.findProjectIdsByUserIdAndNow(eq(2), any()))
            .thenReturn(Set.of(3, 4));
        var jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user@example.com");
        var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        var securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (var mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            var result = securityProjectsValidator.validate(1, operations);

            assertFalse(result);
        }
    }

    @Test
    void validate_rolePmCommonProjects_true() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(operations.hasAnyRole("HR", "ADMIN")).thenReturn(false);
        when(operations.hasRole("PM")).thenReturn(true);
        var pmUser = mock(User.class);
        when(pmUser.getId()).thenReturn(2);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(pmUser));
        when(userProjectRepository.findProjectIdsByUserIdAndNow(eq(1), any()))
            .thenReturn(Set.of(1, 2));
        when(userProjectRepository.findProjectIdsByUserIdAndNow(eq(2), any()))
            .thenReturn(Set.of(2, 3));
        var jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user@example.com");
        var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        var securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (var mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            var result = securityProjectsValidator.validate(1, operations);

            assertTrue(result);
        }
    }

    @Test
    void validate_rolePmWrongPrincipal_false() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(operations.hasAnyRole("HR", "ADMIN")).thenReturn(false);
        when(operations.hasRole("PM")).thenReturn(true);
        var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new Object());
        var securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (var mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            var result = securityProjectsValidator.validate(1, operations);

            assertFalse(result);
        }
        verify(userRepository, never()).findByEmail(any());
        verify(userProjectRepository, never()).findProjectIdsByUserIdAndNow(any(), any());
    }

    @Test
    void validate_userNotExists_throwsNotFound() {
        when(userRepository.existsById(1)).thenReturn(false);

        assertThrows(NotFoundException.class,
            () -> securityProjectsValidator.validate(1, operations)
        );

        verify(operations, never()).hasAnyRole("HR", "ADMIN");
        verify(operations, never()).hasRole("PM");
        verify(userRepository, never()).findByEmail(any());
        verify(userProjectRepository, never()).findProjectIdsByUserIdAndNow(any(), any());
    }

    @Test
    void validate_pmUserNotExists_throwsNotFoundException() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(operations.hasAnyRole("HR", "ADMIN")).thenReturn(false);
        when(operations.hasRole("PM")).thenReturn(true);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        var jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user@example.com");
        var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        var securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (var mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThrows(NotFoundException.class,
                () -> securityProjectsValidator.validate(1, operations)
            );
        }

        verify(userProjectRepository, never()).findProjectIdsByUserIdAndNow(any(), any());
    }
}
