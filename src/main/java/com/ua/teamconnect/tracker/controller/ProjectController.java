package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.dto.ProjectDto;
import com.ua.teamconnect.tracker.service.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/projects")
@RequiredArgsConstructor
@Tag(name = "Project Controller", description = "Endpoints related to projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping(params = "userId")
    @PreAuthorize("@securityProjectsValidator.validate(#userId, #root)")
    @Tag(name = "Find projects for user", description = "Find projects for specified user")
    public List<ProjectDto> findForUser(@RequestParam Integer userId) {
        return projectService.findByUserId(userId);
    }

    @GetMapping(params = "!userId")
    @Tag(name = "Find personal projects", description = "Find projects for user from token")
    public List<ProjectDto> findForToken(@AuthenticationPrincipal Jwt jwt) {
        return projectService.findByUserEmail(jwt.getSubject());
    }
}
