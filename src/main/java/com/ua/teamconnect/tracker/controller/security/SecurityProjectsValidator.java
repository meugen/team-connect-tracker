package com.ua.teamconnect.tracker.controller.security;

import com.ua.teamconnect.tracker.model.exception.NotFoundException;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SecurityProjectsValidator {

    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;

    public boolean validate(@NonNull Integer userId, @NonNull MethodSecurityExpressionOperations operations) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(operations);

        if (!userRepository.existsById(userId)) {
            throw NotFoundException.userById(userId);
        }
        if (operations.hasAnyRole("HR", "ADMIN")) return true;
        return operations.hasRole("PM") && userHasAccessToPmProjects(userId);
    }

    private boolean userHasAccessToPmProjects(Integer userId) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Jwt jwt)) return false;
        var pmUser = userRepository.findByEmail(jwt.getSubject()).orElseThrow(
            () -> NotFoundException.userByEmail(jwt.getSubject())
        );

        var now = LocalDate.now();
        var userProjects = new HashSet<>(userProjectRepository.findProjectIdsByUserIdAndNow(userId, now));
        var pmProjects = userProjectRepository.findProjectIdsByUserIdAndNow(pmUser.getId(), now);
        userProjects.retainAll(pmProjects);
        return !userProjects.isEmpty();
    }
}
