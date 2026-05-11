package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.annotation.ApiResponseBadRequest;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseOk;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseUnauthorized;
import com.ua.teamconnect.tracker.model.dto.UserAnniversaryDto;
import com.ua.teamconnect.tracker.model.dto.UserProfileDto;
import com.ua.teamconnect.tracker.model.dto.in.AnniversariesDto;
import com.ua.teamconnect.tracker.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User Controller", description = "Endpoints related to users")
@RequiredArgsConstructor
@ApiResponseOk @ApiResponseUnauthorized
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public UserProfileDto profile(@AuthenticationPrincipal Jwt jwt) {
        return userService.profile(jwt.getSubject());
    }

    @GetMapping("/anniversaries")
    @ApiResponseBadRequest
    @Parameter(
        name = "startDate",
        description = "Start date in dd-MM format",
        example = "20-01",
        required = true,
        in = QUERY
    )
    @Parameter(
        name = "endDate",
        description = "End date in dd-MM format",
        example = "20-02",
        required = true,
        in = QUERY
    )
    public List<UserAnniversaryDto> anniversaries(
        @Valid @Parameter(hidden = true) AnniversariesDto anniversaries
    ) {
        return userService.anniversaries(anniversaries);
    }
}
